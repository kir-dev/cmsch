package hu.bme.sch.cmsch.component.bmejegy

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.form.FormService
import hu.bme.sch.cmsch.extending.BmeJegyListener
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.*

const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36"

@Service
@ConditionalOnBean(BmejegyComponent::class)
open class BmejegyService(
    @Value("\${hu.bme.sch.cmsch.component.bmejegy.bmejegyservice.username:}") private val bmejegyUsername: String,
    @Value("\${hu.bme.sch.cmsch.component.bmejegy.bmejegyservice.password:}") private val bmejegyPassword: String,
    private val bmejegy: BmejegyComponent,
    private val objectMapper: ObjectMapper,
    private val clock: TimeService,
    private val bmejegyRecordRepository: BmejegyRecordRepository,
    private val formService: FormService,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val listeners: List<BmeJegyListener>,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val cellMapper = jacksonObjectMapper()

    private fun extractNonceFromResponse(response: ResponseEntity<String>): String {
        val body = response.body ?: ""
        val pattern = "name=\"woocommerce-login-nonce\" value=\""
        val indexOf = body.indexOf(pattern).coerceAtLeast(0)
        return body.substring(indexOf + pattern.length, body.indexOf("\"", indexOf + pattern.length + 1))
    }

    @Transactional(readOnly = true)
    open fun findUserByVoucher(qr: String): Optional<BmejegyRecordEntity> {
        return Optional.ofNullable(bmejegyRecordRepository.findAllByQrCode(qr).firstOrNull())
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun updateTickets(tickets: BmeJegyResponse) {
        val registered = bmejegyRecordRepository.findAll().associateBy { it.itemId }
        val newTickets = mutableListOf<BmejegyRecordEntity>()
        tickets.rows?.forEach {
            val cell = it.cell
            if (cell != null && !registered.containsKey(cell["order_item_id"])) {
                newTickets.add(BmejegyRecordEntity(
                    id = 0,
                    item = cell["order_item_name"] ?: "",
                    fullName = cell["full_name"] ?: "",
                    status = cell["post_status"] ?: "N/A",
                    orderKey = cell["order_key"] ?: "",
                    email = cell["email"] ?: "",
                    qrCode = cell["voucher_code"] ?: "INVALID",
                    photoId = cell[bmejegy.szigFieldName.getValue()]?.uppercase() ?: "",
                    date = cell["post_date"] ?: "",
                    registered = clock.getTimeInSeconds(),
                    idId = cell["id"] ?: "",
                    itemId = cell["order_item_id"] ?: "",
                    total = cell["line_total"] ?: "",
                    faculty = "",
                    matchedUserId = 0,
                    rawData = cellMapper.writeValueAsString(cell)
                ))
                listeners.forEach { listener -> listener.onTicketRaw(it.cell) }
            }
        }
        log.info("[BMEJEGY] Found new tickets: {}", newTickets.size)
        bmejegyRecordRepository.saveAll(newTickets)
        newTickets.forEach { ticket ->
            listeners.forEach { listener -> listener.onTicketAdded(ticket) }
        }
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun updateUserStatuses() {
        val unmatched = bmejegyRecordRepository.findAllByMatchedUserId(0)
        val changedTickets = mutableListOf<BmejegyRecordEntity>()
        val changedUsers = mutableListOf<UserEntity>()
        val userToTicketMapping = mutableListOf<Pair<UserEntity, BmejegyRecordEntity>>()

        if (bmejegy.completeByPhotoId.isValueTrue()) {
            log.info("[BMEJEGY] Completing by photoId")

            val reader = objectMapper.readerFor(object : TypeReference<MutableMap<String, String>>() {})
            val forms = formService.getSelectedForms()

            val group1 = if (bmejegy.grantGroupName1.getValue().isNotBlank())
                groupRepository.findByName(bmejegy.grantGroupName1.getValue()).orElse(null) else null
            val group2 = if (bmejegy.grantGroupName2.getValue().isNotBlank())
                groupRepository.findByName(bmejegy.grantGroupName2.getValue()).orElse(null) else null
            val group3 = if (bmejegy.grantGroupName3.getValue().isNotBlank())
                groupRepository.findByName(bmejegy.grantGroupName3.getValue()).orElse(null) else null

            forms.forEach { form ->
                formService.getSubmissions(form).forEach { raw ->
                    val submission = reader.readValue<MutableMap<String, String>>(raw.submission)
                    val photoId = (submission[bmejegy.szigFieldName.getValue()] ?: "").uppercase()
                    val ticket = unmatched.firstOrNull { it.photoId == photoId }
                    if (ticket != null) {
                        ticket.matchedUserId = raw.submitterUserId ?: 0
                        val user = updateUser(ticket.matchedUserId, ticket.item, group1, group2, group3)
                        if (user != null) {
                            changedUsers.add(user)
                            userToTicketMapping.add(Pair(user, ticket))
                        }
                        changedTickets.add(ticket)
                    }
                }
            }

        }

        log.info("[BMEJEGY] Newly matched users: {}", changedTickets.size)
        bmejegyRecordRepository.saveAll(changedTickets)

        log.info("[BMEJEGY] Updated users: {}", changedUsers.size)
        userRepository.saveAll(changedUsers)

        if (listeners.isNotEmpty()) {
            userToTicketMapping.forEach { mapping ->
                listeners.forEach { listener -> listener.onTicketAssigned(mapping.first, mapping.second) }
            }
        }
    }

    private fun updateUser(
        submitterUserId: Int,
        item: String,
        group1: GroupEntity?,
        group2: GroupEntity?,
        group3: GroupEntity?
    ) : UserEntity? {
        val userEntityOptional = userRepository.findById(submitterUserId)
        if (userEntityOptional.isPresent) {
            val user = userEntityOptional.orElseThrow()
            var changed = false

            if (bmejegy.forOrder1.getValue().isNotBlank() && item.contains(bmejegy.forOrder1.getValue())) {
                if (bmejegy.grantAttendee1.isValueTrue() && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.ATTENDEE
                    changed = true
                }
                if (bmejegy.grantPrivileged1.isValueTrue() && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.PRIVILEGED
                    changed = true
                }
                if (group1 != null) {
                    user.group = group1
                    user.groupName = group1.name
                    changed = true
                }
            }

            if (bmejegy.forOrder2.getValue().isNotBlank() && item.contains(bmejegy.forOrder2.getValue())) {
                if (bmejegy.grantAttendee2.isValueTrue() && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.ATTENDEE
                    changed = true
                }
                if (bmejegy.grantPrivileged2.isValueTrue() && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.PRIVILEGED
                    changed = true
                }
                if (group2 != null) {
                    user.group = group2
                    user.groupName = group2.name
                    changed = true
                }
            }

            if (bmejegy.forOrder3.getValue().isNotBlank() && item.contains(bmejegy.forOrder3.getValue())) {
                if (bmejegy.grantAttendee3.isValueTrue() && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.ATTENDEE
                    changed = true
                }
                if (bmejegy.grantPrivileged3.isValueTrue() && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.PRIVILEGED
                    changed = true
                }
                if (group3 != null) {
                    user.group = group3
                    user.groupName = group3.name
                    changed = true
                }
            }

            if (changed) {
                log.info("[BMEJEGY] Updating user {} to role={} group={}", user.fullName, user.role.name, user.groupName)
                return user
            }
        }
        return null
    }

    fun fetchData(): BmeJegyResponse {
        log.info("[BMEJEGY] Fetching started")

        val strategies: ExchangeStrategies = ExchangeStrategies.builder()
            .codecs { codecs -> codecs.defaultCodecs().maxInMemorySize(bmejegy.bufferSize.getIntValue(262144)) }
            .build()

        val client = WebClient.builder()
            .baseUrl("https://www.bmejegy.hu")
            .clientConnector(
                ReactorClientHttpConnector(HttpClient.create()
                    .followRedirect(true)
                    .keepAlive(true)
                )
            )
            .exchangeStrategies(strategies)
            .build()

        val responseLogin1 = client.get()
            .uri("/fiokom/")
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        val nonce = extractNonceFromResponse(responseLogin1)
        log.info("[BMEJEGY] Nonce ok, {}", nonce)

        val login = client.post()
            .uri("/fiokom/")
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(
                BodyInserters.fromFormData("username", bmejegyUsername)
                    .with("password", bmejegyPassword)
                    .with("login", "Bejelentkezés")
                    .with("_wp_http_referer", "/fiokom/")
                    .with("woocommerce-login-nonce", nonce)
            )
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        log.info("[BMEJEGY] Login {}", login.statusCode)

        val sessionCookieWpAdmin = login.headers["Set-Cookie"]?.get(1)?.split("=", ";")
            ?: throw IllegalStateException("Invalid cookie")
        val sessionCookieSite = login.headers["Set-Cookie"]?.get(2)?.split("=", ";")
            ?: throw IllegalStateException("Invalid cookie")

        val responseLogin2 = client.get()
            .uri("/fiokom/")
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        log.info("[BMEJEGY] Account {}", responseLogin2.statusCode)

        val reportResponse = client.get()
            .uri("/rendeles-riportok/")
            .header(HttpHeaders.USER_AGENT, USER_AGENT)
            .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML)
            .cookie(sessionCookieSite[0], sessionCookieSite[1])
            .retrieve()
            .toEntity(String::class.java)
            .block()!!

        log.info("[BMEJEGY] Reports GET {}", reportResponse.statusCode)

        val results = mutableListOf<BmejegyRow>()
        var page = 0
        while (true) {
            ++page
            val ajaxResponse = client.post()
                .uri {
                    it.path("/wp-admin/admin-ajax.php")
                        .queryParam("action", "onliner_ajax")
                        .queryParam("onliner_ajax_action", "jqgrid_order_result")
                        .build()
                }
                .header(HttpHeaders.USER_AGENT, USER_AGENT)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Host", "www.bmejegy.hu")
                .header("Origin", "https://www.bmejegy.hu")
                .header("Referer", "https://www.bmejegy.hu/rendeles-riportok/")
                .header(
                    "Cookie", sessionCookieWpAdmin[0] + "=" + sessionCookieWpAdmin[1] + "; "
                            + sessionCookieSite[0] + "=" + sessionCookieSite[1] + "; " +
                            "euCookie=set; _icl_current_language=hu"
                )
                .body(
                    BodyInserters.fromFormData("_search", "false")
                        .with("nd", bmejegy.minTimestamp.getValue())
                        .with("rows", bmejegy.countToFetch.getValue())
                        .with("page", page.toString())
                        .with("sidx", "")
                        .with("sord", "asc")
                )
                .retrieve()
                .toEntity(String::class.java)
                .block()!!


            val ajaxBody = ajaxResponse.body
            log.info("[BMEJEGY] Partial response page:{} is {} long", page, ajaxBody?.length ?: -1)
            log.info("[BMEJEGY] Result: $ajaxBody")
            val response = objectMapper.readerFor(BmeJegyResponse::class.java).readValue<BmeJegyResponse>(ajaxBody ?: "{}")
            results.addAll(response?.rows ?: listOf())

            if (response == null || page == response.total || response.records?.isEmpty() != false) {
                break
            }
        }

        log.info("[BMEJEGY] Finished! Found: ${results.size}")
        return BmeJegyResponse(page = page.toString(), total = page, records = results.size.toString(), rows = results)
    }

    @Transactional(readOnly = true)
    open fun findVoucherByUser(userId: Int): Optional<String> {
        return Optional.ofNullable(bmejegyRecordRepository.findAllByMatchedUserId(userId).firstOrNull())
            .map { it.qrCode }
    }

}
