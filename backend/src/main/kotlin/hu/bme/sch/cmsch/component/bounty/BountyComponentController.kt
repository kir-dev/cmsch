package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/bounty")
@ConditionalOnBean(BountyComponent::class)
class BountyComponentController(
    adminMenuService: AdminMenuService,
    component: BountyComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    BountyComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_BOUNTY,
    componentCategoryName = "Fejvadászat",
    componentMenuName = "Beállítások",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
    A **Fejvadászat** egy gyilkosos: minden induló csapat kap egy célpont csapatot és egy fegyvert, és a célpont csapat tagjait kell kiiktatnia úgy, hogy a kisorsolt fegyverrel beolvassák az áldozat titkos kódját. Aki sokáig nem gyilkol, inaktivitás miatt kiesik.

    ## A játék menete

    1. **Menüpont** – a **Menü beállítások** oldalon tedd láthatóvá a **Fejvadászat** menüpontot. A komponens menüpontja alapból rejtett, így e nélkül a játékosok nem érik el az oldalt.
    2. **Kör létrehozása** – a **Körök** menüpontban add meg a **Kör neve**, **Regisztráció kezdete**, **Regisztráció vége**, **Játék kezdete**, **Játék vége**, **Nehézség** és **Inaktivitási idő (óra)** mezőket. Több kör futhat egyszerre; a 0 dátumú (még készülő) köröket a rendszer nem indítja el.
    3. **Regisztráció** – a játékosok egyénileg, körönként jelentkeznek az infópultnál. A **Regisztráció** menüponttal olvasd be a profiljuk QR kódját: a rendszer a nyitott regisztrációs időszakú, még nem lezárt körre regisztrálja őket, és a csapatukat a regisztráció pillanatában rögzíti (a későbbi csapatváltás nem számít). Csak csapattal rendelkező játékos regisztrálható. A még nem regisztrált játékosok a **Fejvadászat** oldal tetején látják a saját QR kódjukat.
    4. **Sorsolás** – a **Regisztráció vége** után a rendszer magától kiosztja a célpontokat és a fegyvereket, és a kört **Inicializálva** állapotba teszi. Ehhez legalább **2 különböző csapat** kell. Ha ennél kevesebb van, a kör sorsolás nélkül marad, és amint egy későbbi regisztrációval (például a regisztrációs időszak meghosszabbításával) összejön a 2 csapat, a következő percben megtörténik a sorsolás.
    5. **Játék** – a **Játék kezdete** után a játékos a körkártyáján látja a célpontját, a fegyverét, az élő létszámokat, a hátralévő idejét és a saját titkos kódját (ezt mutatja fel az áldozat). A **Gyilkolás** gombbal az áldozat kódja QR-kódról beolvasva vagy kézzel beírva küldhető be; csak a célpont csapat élő tagja ölhető meg, és egy kód csak a saját körében érvényes. A gyilkosság pontot ér (a pontokat csak a szervezők látják az admin oldalakon; a játékosok csak a helyezésüket és a gyilkosságaik számát látják), és újraindítja a gyilkos visszaszámlálóját.
    6. **Kör vége** – a **Játék vége** leteltével, vagy ha már csak egy csapat él, a rendszer lezárja a kört: az élő csapatok gyilkosságszám szerint, a kiesettek a kiesésük **fordított** sorrendje szerint (aki legutoljára esett ki, az a 2. helyezett) kapnak **Helyezés**t, a **Túlélási pontok** pedig a **Túlélási pontok alapja** beállítás osztva a helyezéssel. Holtversenynél az élő csapatok azonos helyezést kapnak, és mindegyikük a teljes alappontszámot, így több **Győztes** is lehet.

    ## Ami magától történik (percenként)

    - A sorsolás a **Regisztráció vége** után, a kör lezárása a **Játék vége** után legkésőbb egy perccel következik be.
    - A **Játék kezdete**kor induló visszaszámláló lejártával a játékos kiesik; erről a körkártya, illetve a **Kiesés időpontja** és az **Inaktivitás miatt esett ki** mezők árulkodnak.
    - Ha egy csapat utolsó tagja is kiesik, a rá vadászó csapat megörökli az áldozat csapat célpontját és fegyverét.

    ## Beállítások, amik a játékot alakítják

    | Beállítás | Hatás |
    | --- | --- |
    | **Könnyű / Közepes / Nehéz fegyverek** | vesszővel elválasztott fegyverlista; a kör **Nehézsége** dönti el, melyik listából sorsol a rendszer. Ugyanaz a fegyver több csapathoz is kerülhet. |
    | **Könnyű / Közepes / Nehéz gyilkosság pontszáma** | egy gyilkosságért járó pont az adott nehézségű körben (alapértéke 20 / 30 / 40). |
    | **Túlélési pontok alapja** | az 1. helyezett csapat túlélési pontja a kör végén; a többi helyezett ennyit kap osztva a saját helyezésével, a 2. hely ennek fele, 3. hely ennek a harmada... A kör lezárásakor érvényes érték számít, utólag nem lehet változtatni. |

    ## Admin oldalak

    - **Fejvadászat körök** – a körök felvétele és szerkesztése; az **Inicializálva** és a **Lezárva** kapcsolót a rendszer tartja karban.
    - **Fejvadászat regisztrációk** – játékosonkénti sorok (**Titkos kód**, **Életben van**, **Inaktivitási határidő**). Új sor csak a QR olvasóval keletkezik, kézzel nem vehető fel, de szerkeszthető és törölhető.
    - **Fejvadászat csapatok** – csak megtekinthető: célpont, fegyver, kiesés, helyezés, pontok.
    - **Fejvadászat gyilkosságok** – csak megtekinthető napló (gyilkos, áldozat, pont, időpont).

    ## Figyelmeztetés

    A rendszer által kezelt értékeket ne módosítsd, mert az hibát okozhat! **Szólj egy Kir-Devesnek, ha gond van.**
    """
) {
    init {
        adminMenuService.registerEntry(BountyComponent::class.java.simpleName, AdminMenuEntry(
            "Regisztráció",
            "qr_code_scanner",
            "/admin/bounty-registration/",
            1,
            StaffPermissions.PERMISSION_REGISTER_BOUNTY
        ))
    }
}
