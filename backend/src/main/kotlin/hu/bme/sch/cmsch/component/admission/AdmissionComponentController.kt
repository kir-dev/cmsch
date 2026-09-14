package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/admission")
@ConditionalOnBean(AdmissionComponent::class)
class AdmissionComponentController(
    adminMenuService: AdminMenuService,
    component: AdmissionComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    AdmissionComponent::class.java,
    component,
        ControlPermissions.PERMISSION_CONTROL_ADMISSION,
        componentCategoryName = "Beléptetés",
        componentMenuName = "Jogosultságok",
        menuService = menuService,
        auditLogService = auditLogService,
        storageService = storageService,
        documentationMarkdown = """
    A **Beléptetés** komponens a helyszíni beengedést végzi: a rendező beolvassa a belépő kódját, a rendszer pedig a beállított szabályok alapján dönt. Nyilvános (résztvevői) menüpontja nincs, minden funkció a rendezői menü **Beléptetés** kategóriájában érhető el.

    ## Beengedés menete

    1. A **Jogosultságok** menüpontban állítsd be, kiket engedj be (lásd lent).
    2. A beolvasókhoz a **Beléptetés kezelése**, a naplóhoz a **Beléptetés logok megtekintése** jogosultságot add meg a kijelölt rendezőknek.
    3. A **Beléptetés** menüpontban nyílik a kamerás beolvasó (mobilon érdemes használni). Az eredmény ablakban a **Név**, a **Belépés** (csoport) és a **JOGKÖR** látszik: KITILTVA, NEM JOGOSULT, BELÉPHET, VIP, RENDEZŐ, FELLÉPŐ, FŐRENDEZŐ.
    4. Űrlapos beengedéshez az **Űrlapos beléptetés**, jegyekhez a **Jegyellenőrzés** menüpontot használd.

    ## Jogosultsági szabályok

    A rendszer minden forrásból összegyűjti a jogosultságokat, és a **legmagasabb** elért szintet adja. Ha egyik lista sem tartalmazza a belépőt, a válasz **NEM JOGOSULT** – üres listákkal senki sem jut be.

    - **Csoportok hozzáférése** – a **USER / VIP / PERFORMER / ORGANIZER / LEAD_ORGANIZER hozzáférésű csoportok** mezőkben a csoportnevek pontos, vesszővel elválasztott felsorolása.
    - **Felhasználók hozzáférése** – ugyanezek a szintek CMSCH ID-k alapján.
    - **Szerepek hozzáférése** – a **USER hozzáférés** és az **ORGANIZER hozzáférés** a kiválasztott szerepkörtől felfelé mindenkire vonatkozik.
    - **Tiltó lista** – a **Kitiltott csoportok** és a **Kitiltott felhasználók** minden más szabályt felülírnak, az érintett belépő KITILTVA jelzést kap.

    ## Beállítások (Jogosultságok menüpont)

    | Beállítás | Mit tesz |
    | --- | --- |
    | **Beléptetések mentése** | Minden beolvasás a **Belépés logok** közé és az audit naplóba kerül. Kikapcsolva nem működik az ismételt beolvasás számlálása, és az űrlapos CSV export sem jelöli, ki lépett már be. |
    | **Csak az elfogadott űrlapok számítanak** | Űrlapos beléptetésnél csak az elfogadott és nem elutasított beadás enged be. Csak az űrlapok komponenssel együtt működik. |
    | **BME Jegyesek beengedése** | BME Jegy utalványkódokat is elfogad (csak bekapcsolt bmejegy komponens mellett). |
    | **Belépések számának mutatása** | A **Jegyellenőrzés** beolvasónál kiírja, hányszor olvasták be ugyanazt a kódot; ismétlésnél **NEM ELSŐ!!!** figyelmeztetést ad. |

    ## Beolvasók és oldalak

    - **Beléptetés** – CMSCH profil QR kódot (a beállított előtaggal kezdődik) és BME Jegy utalványkódot ismer fel. A **Jegyek** listát nem használja.
    - **Jegyellenőrzés** – a **Jegyek** listából dolgozik. **A jegy megléte önmagában beenged**, a jegy **Belépési jogosultság** mezője csak a kijelzett jogkört adja meg.
    - **Űrlapos beléptetés** – csak a felhasználók által létrehozott (nem csoport-tulajdonú) űrlapokat listázza, űrlapok komponens nélkül csak figyelmeztetést mutat. A **Beengedés** művelet az adott űrlap beolvasóját nyitja, a **CSV Export** a beadásokat és a belépéseket adja egy táblában (**Beléptetés kimentése** jogosultság kell hozzá). Az elfogadott beadás önmagában BELÉPHET szintet ad, amit a csoport/felhasználó/szerep szabályok emelhetnek; névtelen beadásnál az űrlap token mezőjének kódja is beolvasható.
    - **Jegyek** – egy sor egy jegy, a listában **Tulajdonos**, **Email** és **QR** látszik; szerkesztve adható meg a **Belépési jogosultság**, a **Megjegyzés** és a **Profil QR kód használata** (bekapcsolva a jegy a **QR** mezőben tárolt kóddal, kikapcsolva a belépő profiljához tartozó **Email** címmel azonosítható). Import és export is elérhető.
    - **Belépés logok** – a listában a **Felhasználó**, az **Engedélyezve** és a **Frissült** látszik; a sorok szerkeszthetők és törölhetők, de a törlés az ismételt beolvasás számlálóját is módosítja.
    """
    )
     {
    init {
        adminMenuService.registerEntry(AdmissionComponent::class.java.simpleName, AdminMenuEntry(
            "Beléptetés",
            "mobile_friendly",
            "/admin/admission/",
            1,
            StaffPermissions.PERMISSION_VALIDATE_ADMISSION
        ))
        adminMenuService.registerEntry(AdmissionComponent::class.java.simpleName, AdminMenuEntry(
            "Jegyellenőrzés",
            "mobile_friendly",
            "/admin/admission/ticket",
            6,
            StaffPermissions.PERMISSION_VALIDATE_ADMISSION
        ))
    }
}
