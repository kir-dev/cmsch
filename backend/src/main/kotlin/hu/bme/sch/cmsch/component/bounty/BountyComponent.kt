package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.bounty"])
class BountyComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "bounty",
    "/bounty",
    "Fejvadászat",
    ControlPermissions.PERMISSION_CONTROL_BOUNTY,
    listOf(BountyRoundEntity::class, BountyRegistrationEntity::class, BountyTeamEntity::class, BountyKillEntity::class),
    env
) {

    val bountyGroup by SettingGroup(fieldName = "Fejvadászat")

    final var title by StringSettingRef("Fejvadászat",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Fejvadászat", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Mely szerepkörökkel nyitható meg az oldal")

    var topMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "A játék leírása. Ha üres, nem jelenik meg.")

    val weaponPoolsGroup by SettingGroup(fieldName = "Fegyver poolok")

    var easyWeapons by StringSettingRef("Repohár, Vízpisztoly", serverSideOnly = true, fieldName = "Könnyű fegyverek",
        description = "Vesszővel elválasztva. A könnyű körökben ezekből sorsol a rendszer.")

    var mediumWeapons by StringSettingRef("Golyóstoll, Zokni", serverSideOnly = true, fieldName = "Közepes fegyverek",
        description = "Vesszővel elválasztva. A közepes körökben ezekből sorsol a rendszer.")

    var hardWeapons by StringSettingRef("Madártoll, Cérna", serverSideOnly = true, fieldName = "Nehéz fegyverek",
        description = "Vesszővel elválasztva. A nehéz körökben ezekből sorsol a rendszer.")

    val scoringGroup by SettingGroup(fieldName = "Pontozás")

    var easyKillPoints by NumberSettingRef(defaultValue = 20,
        serverSideOnly = true,
        fieldName = "Könnyű gyilkosság pontszáma",
        description = "Egy gyilkosságért kapott pont a könnyű körökben")

    var mediumKillPoints by NumberSettingRef(defaultValue = 30,
        serverSideOnly = true,
        fieldName = "Közepes gyilkosság pontszáma",
        description = "Egy gyilkosságért kapott pont a közepes körökben")

    var hardKillPoints by NumberSettingRef(defaultValue = 40,
        serverSideOnly = true,
        fieldName = "Nehéz gyilkosság pontszáma",
        description = "Egy gyilkosságért kapott pont a nehéz körökben")

    var survivalPointsBase by NumberSettingRef(defaultValue = 5000,
        serverSideOnly = true,
        fieldName = "Túlélési pontok alapja",
        description = "Az 1. helyezett csapat túlélési pontja; a többi helyezett ennyit kap osztva a helyezésével")

    val cardTextsGroup by SettingGroup(fieldName = "Az oldalon megjelenő szövegek")

    var easyRoundLabel by StringSettingRef("Könnyű 🥰", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Könnyű kör felirata",
        description = "A kör neve mellett megjelenő nehézség felirat a könnyű köröknél")

    var mediumRoundLabel by StringSettingRef("Közepes 😅", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Közepes kör felirata",
        description = "A kör neve mellett megjelenő nehézség felirat a közepes köröknél")

    var hardRoundLabel by StringSettingRef("Nehéz 😣", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Nehéz kör felirata",
        description = "A kör neve mellett megjelenő nehézség felirat a nehéz köröknél")

    var registrationInfo by StringSettingRef(
        defaultValue = "A regisztráció az **infópultnál** történik.\n\nJelentkezz most, még nyitva a regisztráció!",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Regisztrációs szöveg",
        description = "Amikor a játékos még nincs regisztrálva a körbe, ez a szöveg jelenik meg a kör kártyáján")

    var killedMessage by StringSettingRef("Meghaltál ebben a körben.", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Kiesés üzenet",
        description = "Ez jelenik meg, ha a játékost megölték")

    var eliminatedByInactivityMessage by StringSettingRef("Inaktivitás miatt kiestél ebből a körből.",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Inaktivitás miatti kiesés üzenet",
        description = "Ez jelenik meg, ha a játékos inaktivitás miatt esett ki")

    var teamEliminatedMessage by StringSettingRef("A csapatod kiesett ebben a körben.",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csapat kiesés üzenet",
        description = "Ez jelenik meg, ha az egész csapat kiesett")

    var winnerMessage by StringSettingRef("A csapatod megnyerte a kört!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Győzelem üzenet",
        description = "Ez jelenik meg, ha a csapat megnyerte a kört")

}
