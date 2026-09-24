package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random
import kotlin.uuid.Uuid

const val BOUNTY_QR_PREFIX = "bounty:"

@Service
@ConditionalOnBean(BountyComponent::class)
class BountyService(
    private val bountyRoundRepository: BountyRoundRepository,
    private val bountyRegistrationRepository: BountyRegistrationRepository,
    private val bountyTeamRepository: BountyTeamRepository,
    private val bountyKillRepository: BountyKillRepository,
    private val userService: UserService,
    private val clock: TimeService,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val bountyComponent: BountyComponent,
) {

    private val log = LoggerFactory.getLogger(javaClass)


    @Transactional(readOnly = true)
    fun previewRegistrationByCmschId(rawCmschId: String): BountyRegistrationResponse {
        val candidate = findRegistrationCandidate(rawCmschId)
            ?: return registrationError(rawCmschId)
        if (bountyRegistrationRepository.findByRoundIdAndUserId(candidate.round.id, candidate.user.id) != null) {
            return BountyRegistrationResponse(
                false, "A játékos már regisztrálva van a(z) ${candidate.round.name} körre",
                candidate.user.fullName, candidate.group.name, candidate.round.name, candidate.isNewTeam,
                seatsRemaining(candidate.round)
            )
        }
        if (isRegistrationFull(candidate.round)) {
            return registrationFullResponse(candidate)
        }
        return BountyRegistrationResponse(
            true, "Ellenőrzés sikeres", candidate.user.fullName, candidate.group.name,
            candidate.round.name, candidate.isNewTeam, seatsRemaining(candidate.round)
        )
    }

    @Retryable(value = [SQLException::class], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun registerByCmschId(rawCmschId: String): BountyRegistrationResponse {
        val candidate = findRegistrationCandidate(rawCmschId)
            ?: return registrationError(rawCmschId)

        if (bountyRegistrationRepository.findByRoundIdAndUserId(candidate.round.id, candidate.user.id) != null) {
            return BountyRegistrationResponse(
                false, "A játékos már regisztrálva van a(z) ${candidate.round.name} körre",
                candidate.user.fullName, candidate.group.name, candidate.round.name, candidate.isNewTeam,
                seatsRemaining(candidate.round)
            )
        }
        if (isRegistrationFull(candidate.round)) {
            return registrationFullResponse(candidate)
        }

        bountyRegistrationRepository.save(BountyRegistrationEntity(
            roundId = candidate.round.id,
            roundName = candidate.round.name,
            userId = candidate.user.id,
            userName = candidate.user.fullName,
            groupId = candidate.group.id,
            groupName = candidate.group.name,
            code = Uuid.generateV7().toString(),
            alive = true,
            deadline = 0,
        ))
        log.info("User '{}' (group: '{}') registered for bounty round '{}'", candidate.user.fullName, candidate.group.name, candidate.round.name)
        return BountyRegistrationResponse(
            true, "Sikeres regisztráció", candidate.user.fullName, candidate.group.name,
            candidate.round.name, candidate.isNewTeam, seatsRemaining(candidate.round)
        )
    }

    private fun findRegistrationCandidate(rawCmschId: String): RegistrationCandidate? {
        // Accept both the plain profile QR and the bounty page's prefixed QR ("bounty:<cmschId>")
        val cmschId = rawCmschId.removePrefix(BOUNTY_QR_PREFIX)
        if (!cmschId.startsWith(startupPropertyConfig.profileQrPrefix)) return null

        val user = userService.searchByCmschId(cmschId).orElse(null) ?: return null
        val group = user.group ?: return null
        val now = clock.getTimeInSeconds()
        val round = findOpenRegistrationRound(now) ?: return null
        val isNewTeam = bountyRegistrationRepository.findAllByRoundId(round.id)
            .none { it.groupId == group.id }
        return RegistrationCandidate(user, group, round, isNewTeam)
    }

    private fun isRegistrationFull(round: BountyRoundEntity): Boolean =
        round.registrationLimit != -1 && seatsRemaining(round) == 0

    private fun seatsRemaining(round: BountyRoundEntity): Int? =
        if (round.registrationLimit == -1) null
        else (round.registrationLimit - bountyRegistrationRepository.countByRoundId(round.id)).coerceAtLeast(0)

    private fun findOpenRegistrationRound(now: Long): BountyRoundEntity? = bountyRoundRepository.findAllByOrderByGameStartAsc()
        .filter { clock.inRange(it.registrationStart, it.registrationEnd, now) && !it.finalized }
        .minByOrNull { it.registrationStart }

    @Transactional(readOnly = true)
    fun getRegistrationCapacity(): BountyRegistrationCapacity? {
        val round = findOpenRegistrationRound(clock.getTimeInSeconds()) ?: return null
        return BountyRegistrationCapacity(round.name, seatsRemaining(round))
    }

    private fun registrationFullResponse(candidate: RegistrationCandidate) = BountyRegistrationResponse(
        false, bountyComponent.registrationFullMessage,
        candidate.user.fullName, candidate.group.name, candidate.round.name, candidate.isNewTeam, 0
    )

    private fun registrationError(rawCmschId: String): BountyRegistrationResponse {
        val cmschId = rawCmschId.removePrefix(BOUNTY_QR_PREFIX)
        if (!cmschId.startsWith(startupPropertyConfig.profileQrPrefix))
            return BountyRegistrationResponse(false, "Érvénytelen QR kód")

        val user = userService.searchByCmschId(cmschId).orElse(null)
            ?: return BountyRegistrationResponse(false, "Nincs ilyen felhasználó")
        if (user.group == null) return BountyRegistrationResponse(false, "A felhasználónak nincs csapata")
        return BountyRegistrationResponse(false, "Jelenleg nincs nyitott regisztrációs időszak")
    }

    private data class RegistrationCandidate(
        val user: UserEntity,
        val group: GroupEntity,
        val round: BountyRoundEntity,
        val isNewTeam: Boolean,
    )

    @Transactional(readOnly = true)
    fun getState(user: CmschUser?): BountyView {
        val now = clock.getTimeInSeconds()
        val rounds = bountyRoundRepository.findAllByOrderByGameStartAsc()
        val entity = user?.internalId?.let { userService.findByInternalId(it).getOrNull() }
        return BountyView(
            rounds = rounds.map { mapRound(it, user, now) },
            myRegistrationQr = entity?.cmschId
                ?.takeIf { it.isNotBlank() }
                ?.let { BOUNTY_QR_PREFIX + it },
            myGroupId = user?.groupId,
        )
    }

    private fun mapRound(round: BountyRoundEntity, user: CmschUser?, now: Long): BountyRoundView {
        val phase = when {
            round.finalized -> BountyPhase.FINISHED
            now < round.registrationStart -> BountyPhase.BEFORE_REGISTRATION
            now <= round.registrationEnd -> BountyPhase.REGISTRATION
            now < round.gameStart -> BountyPhase.BEFORE_GAME
            else -> BountyPhase.ACTIVE
        }

        val myRegistrationEntity = user?.id
            ?.let { bountyRegistrationRepository.findByRoundIdAndUserId(round.id, it) }
        val myRegistration = myRegistrationEntity?.let { reg ->
            BountyRegistrationView(
                alive = reg.alive,
                code = if (reg.alive && !round.finalized) reg.code else null,
                deadline = if (reg.alive && reg.deadline > 0) reg.deadline else null,
                eliminatedAt = reg.eliminatedAt,
                eliminatedByInactivity = reg.eliminatedByInactivity,
            )
        }

        // The game is played with the team captured at registration time, not the user's current group
        val myTeam = myRegistrationEntity
            ?.let { bountyTeamRepository.findByRoundIdAndGroupId(round.id, it.groupId) }
            ?.let { team -> mapTeam(round, team) }

        val winnerGroupName = if (round.finalized) {
            bountyTeamRepository.findAllByRoundId(round.id)
                .filter { it.winner }
                .joinToString(", ") { it.groupName }
                .ifBlank { null }
        } else null

        return BountyRoundView(
            id = round.id,
            name = round.name,
            difficulty = round.difficulty.name,
            registrationStart = round.registrationStart,
            registrationEnd = round.registrationEnd,
            registrationFull = isRegistrationFull(round),
            gameStart = round.gameStart,
            gameEnd = round.gameEnd,
            phase = phase,
            finalized = round.finalized,
            winnerGroupName = winnerGroupName,
            myRegistration = myRegistration,
            myTeam = myTeam,
        )
    }

    private fun mapTeam(round: BountyRoundEntity, team: BountyTeamEntity): BountyTeamView {
        val aliveCount = bountyRegistrationRepository.countByRoundIdAndGroupIdAndAliveTrue(round.id, team.groupId)
        val secretsVisible =
            team.eliminatedAt == null && round.initialized && !round.finalized && team.targetGroupId != null
        val targetGroupId = team.targetGroupId
        val kills = bountyKillRepository.findAllByRoundIdAndKillerGroupId(round.id, team.groupId).size
        return BountyTeamView(
            groupName = team.groupName,
            aliveCount = aliveCount,
            targetGroupName = if (secretsVisible && targetGroupId != null)
                bountyTeamRepository.findByRoundIdAndGroupId(round.id, targetGroupId)?.groupName else null,
            targetAliveCount = if (secretsVisible && targetGroupId != null)
                bountyRegistrationRepository.countByRoundIdAndGroupIdAndAliveTrue(round.id, targetGroupId) else null,
            weapon = if (secretsVisible) team.weapon else null,
            winner = team.winner,
            rank = team.rank,
            kills = kills,
        )
    }

    @Retryable(value = [SQLException::class], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun kill(code: String, killerUser: CmschUser): BountyKillResponse {
        val victim = bountyRegistrationRepository.findByCode(code)
            ?: return BountyKillResponse(false, "Érvénytelen kód")
        val round = bountyRoundRepository.findById(victim.roundId).orElse(null)
            ?: return BountyKillResponse(false, "Érvénytelen kód")

        val killer = bountyRegistrationRepository.findByRoundIdAndUserId(round.id, killerUser.id)
            ?: return BountyKillResponse(false, "Nem regisztráltál erre a körre")
        if (!killer.alive)
            return BountyKillResponse(false, "Már meghaltál ebben a körben")

        val now = clock.getTimeInSeconds()
        if (now < round.gameStart)
            return BountyKillResponse(false, "A kör még nem kezdődött el")
        if (now >= round.gameEnd || round.finalized)
            return BountyKillResponse(false, "A kör már véget ért")

        val killerTeam = bountyTeamRepository.findByRoundIdAndGroupId(round.id, killer.groupId)
            ?: return BountyKillResponse(false, "A csapatod még nem kapott célpontot")
        if (killerTeam.eliminatedAt != null)
            return BountyKillResponse(false, "A csapatod kiesett ebben a körben")

        if (killer.deadline <= now) {
            expireRegistration(round, killer, now)
            return BountyKillResponse(false, "Lejárt az aktív időd, így kiestél a körből")
        }

        if (!victim.alive)
            return BountyKillResponse(false, "A célpont már kiesett")
        if (victim.deadline <= now) {
            expireRegistration(round, victim, now)
            return BountyKillResponse(false, "A célpontod már kiesett")
        }
        if (victim.groupId != killerTeam.targetGroupId)
            return BountyKillResponse(false, "Ő nem a te célpontod")

        val points = killPointsFor(round.difficulty)
        victim.alive = false
        victim.eliminatedAt = now
        bountyRegistrationRepository.save(victim)

        bountyKillRepository.save(BountyKillEntity(
            roundId = round.id,
            roundName = round.name,
            killerUserId = killer.userId,
            killerUserName = killer.userName,
            killerGroupId = killerTeam.groupId,
            killerGroupName = killerTeam.groupName,
            victimUserId = victim.userId,
            victimUserName = victim.userName,
            points = points,
            createdAt = now,
        ))

        killer.deadline = now + round.inactivityHours * 3600
        bountyRegistrationRepository.save(killer)

        log.info("User '{}' killed '{}' in bounty round '{}'", killer.userName, victim.userName, round.name)

        var message = "Sikeres gyilkosság!"
        handleTeamElimination(round, victim.groupId, now)?.let { message += "\n$it" }
        return BountyKillResponse(true, message)
    }

    private fun expireRegistration(round: BountyRoundEntity, registration: BountyRegistrationEntity, now: Long) {
        if (!registration.alive || round.finalized)
            return
        registration.alive = false
        registration.eliminatedAt = now
        registration.eliminatedByInactivity = true
        bountyRegistrationRepository.save(registration)
        log.info("User '{}' expired due to inactivity in bounty round '{}'", registration.userName, round.name)
        handleTeamElimination(round, registration.groupId, now)
    }

    private fun handleTeamElimination(round: BountyRoundEntity, groupId: Int, now: Long): String? {
        if (round.finalized)
            return null
        if (bountyRegistrationRepository.countByRoundIdAndGroupIdAndAliveTrue(round.id, groupId) > 0)
            return null
        val victimTeam = bountyTeamRepository.findByRoundIdAndGroupId(round.id, groupId) ?: return null
        if (victimTeam.eliminatedAt != null)
            return null
        victimTeam.eliminatedAt = now
        bountyTeamRepository.save(victimTeam)
        log.info("Team '{}' eliminated in bounty round '{}'", victimTeam.groupName, round.name)

        val remaining = bountyTeamRepository.findAllByRoundIdAndEliminatedAtIsNull(round.id)
        if (remaining.size == 1) {
            finalizeRound(round.id)
            return null
        }

        val hunter = remaining.firstOrNull { it.targetGroupId == victimTeam.groupId }
        val inheritedTarget = victimTeam.targetGroupId
        if (hunter != null && inheritedTarget != null) {
            val newTargetName = bountyTeamRepository.findByRoundIdAndGroupId(round.id, inheritedTarget)?.groupName
            hunter.targetGroupId = inheritedTarget
            hunter.targetGroupName = newTargetName ?: ""
            hunter.weapon = victimTeam.weapon
            bountyTeamRepository.save(hunter)
            return "A csapatod új célpontja: ${newTargetName ?: "?"}, új fegyver: ${hunter.weapon}"
        }
        return null
    }

    private fun finalizeRound(roundId: Int) {
        val round = bountyRoundRepository.findById(roundId).orElse(null) ?: return
        if (round.finalized)
            return
        val teams = bountyTeamRepository.findAllByRoundId(roundId)
        if (teams.isEmpty()) {
            round.finalized = true
            bountyRoundRepository.save(round)
            return
        }

        val killsByGroup = bountyKillRepository.findAllByRoundId(roundId)
            .groupingBy { it.killerGroupId }.eachCount()

        fun killsOf(groupId: Int) = (killsByGroup[groupId] ?: 0).toLong()

        val survivalBase = bountyComponent.survivalPointsBase

        val aliveTeams = teams.filter { it.eliminatedAt == null }
            .sortedWith(compareByDescending<BountyTeamEntity> { killsOf(it.groupId) }.thenBy { it.id })
        val deadTeams = teams.filter { it.eliminatedAt != null }
            .sortedWith(compareByDescending<BountyTeamEntity> { it.eliminatedAt }.thenBy { it.id })

        val (aliveRanks, deadRanks) = assignBountyRanking(aliveTeams.map { killsOf(it.groupId) }, deadTeams.size)
        aliveTeams.forEachIndexed { index, team ->
            team.rank = aliveRanks[index]
            team.winner = aliveRanks[index] == 1
            team.survivalPoints = survivalBase / aliveRanks[index]
        }
        deadTeams.forEachIndexed { index, team ->
            team.rank = deadRanks[index]
            team.survivalPoints = survivalBase / deadRanks[index]
        }
        bountyTeamRepository.saveAll(aliveTeams + deadTeams)
        round.finalized = true
        bountyRoundRepository.save(round)
        log.info("Bounty round '{}' finalized ({} alive, {} dead)", round.name, aliveTeams.size, deadTeams.size)
    }

    private fun assignBountyRanking(aliveKills: List<Long>, deadTeamCount: Int): Pair<List<Int>, List<Int>> {
        val aliveRanks = mutableListOf<Int>()
        var lastRank = 0
        for ((index, kills) in aliveKills.withIndex()) {
            if (index == 0 || kills != aliveKills[index - 1]) lastRank += 1
            aliveRanks.add(lastRank)
        }
        val deadRanks = (1..deadTeamCount).map { lastRank + it }
        return aliveRanks to deadRanks
    }

    @Retryable(value = [SQLException::class], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun tick() {
        val now = clock.getTimeInSeconds()
        // Rounds with unset dates (0) are still being prepared by the organizers, don't touch them
        val rounds = bountyRoundRepository.findAllByOrderByGameStartAsc()
            .filter { it.registrationEnd > 0 && it.gameEnd > 0 }

        rounds.filter { !it.initialized && now >= it.registrationEnd }
            .forEach { initializeRound(it, now) }

        rounds.filter { it.initialized && !it.finalized && now >= it.gameStart }
            .forEach { round ->
                val expired = bountyRegistrationRepository.findAllByRoundIdAndAliveTrue(round.id)
                    .filter { it.deadline in 1..now }
                    .sortedBy { it.deadline }
                expired.forEach { expireRegistration(round, it, now) }
            }

        rounds.filter { it.initialized && !it.finalized && now >= it.gameEnd }
            .forEach { finalizeRound(it.id) }
    }

    private fun initializeRound(round: BountyRoundEntity, now: Long) {
        val registrations = bountyRegistrationRepository.findAllByRoundIdAndAliveTrue(round.id)
        val registrationsByGroup = registrations.filter { it.groupId != 0 }.groupBy { it.groupId }
        if (registrationsByGroup.size < 2) {
            // Not enough teams yet: leave the round uninitialized so a later tick can still draw it
            log.warn("Bounty round '{}' has fewer than 2 registered teams, skipping cycle generation", round.name)
            return
        }

        round.initialized = true
        bountyRoundRepository.save(round)

        val order = registrationsByGroup.keys.shuffled(Random(round.id.toLong()))
        val weapons = weaponsFor(round.difficulty, order.size)
        val teams = order.mapIndexed { index, groupId ->
            val targetGroupId = order[(index + 1) % order.size]
            BountyTeamEntity(
                roundId = round.id,
                roundName = round.name,
                groupId = groupId,
                groupName = registrationsByGroup.getValue(groupId).first().groupName,
                targetGroupId = targetGroupId,
                targetGroupName = registrationsByGroup.getValue(targetGroupId).first().groupName,
                weapon = weapons[index],
            )
        }
        bountyTeamRepository.saveAll(teams)

        val base = maxOf(round.gameStart, now)
        registrations.forEach { it.deadline = base + round.inactivityHours * 3600 }
        bountyRegistrationRepository.saveAll(registrations)
        log.info("Bounty round '{}' initialized with {} teams", round.name, teams.size)
    }

    private fun killPointsFor(difficulty: BountyDifficulty): Long = when (difficulty) {
        BountyDifficulty.EASY -> bountyComponent.easyKillPoints
        BountyDifficulty.MEDIUM -> bountyComponent.mediumKillPoints
        BountyDifficulty.HARD -> bountyComponent.hardKillPoints
    }

    private fun weaponsFor(difficulty: BountyDifficulty, count: Int): List<String> {
        val pool = when (difficulty) {
            BountyDifficulty.EASY -> bountyComponent.easyWeapons
            BountyDifficulty.MEDIUM -> bountyComponent.mediumWeapons
            BountyDifficulty.HARD -> bountyComponent.hardWeapons
        }.split(",").map { it.trim() }.filter { it.isNotBlank() }
        if (pool.isEmpty())
            return List(count) { "Ismeretlen fegyver" }
        val random = Random(System.currentTimeMillis())
        return List(count) { pool[random.nextInt(pool.size)] }
    }

}
