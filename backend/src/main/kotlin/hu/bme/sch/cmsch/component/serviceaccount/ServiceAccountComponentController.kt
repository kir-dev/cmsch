package hu.bme.sch.cmsch.component.serviceaccount

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/service-accounts")
@ConditionalOnBean(ServiceAccountComponent::class)
class ServiceAccountComponentController(
    adminMenuService: AdminMenuService,
    component: ServiceAccountComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ServiceAccountComponent::class.java,
    component,
    ImplicitPermissions.PERMISSION_NOBODY,
    "Service Account",
    "Service Accountok testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Service Account** komponens külső rendszerek számára biztosít programozott hozzáférést (API-kulcsok segítségével) a CMSCH adataihoz.

## Beállítások

A komponensnek nincsenek bonyolult globális beállításai:

- **Jogosultságok** – mely szerepkörökkel kezelhetőek a service account kulcsok.

## Kulcsok kezelése

A **Service Account** menüpont alatt:

- **Új kulcs létrehozása** – API-kulcs generálása külső alkalmazások számára.
- **Szerkesztés / Törlés** – kulcsok kezelése és visszavonása.

## Kulcs létrehozása / szerkesztése

- **Név** – a kulcs megnevezése (pl. "Mobil App" vagy "Statisztika modul").
- **Kulcs** – az egyedi azonosító, amit a külső rendszernek meg kell adnia.
- **Jogosultságok** – meghatározható, hogy az adott kulccsal milyen adatok érhetőek el.

## Használati tippek

- Soha ne add ki a kulcsokat illetékteleneknek, mert azzal teljes hozzáférést kaphatnak a konfigurált adatokhoz.
- Minden külső rendszerhez külön kulcsot használj, hogy szükség esetén egyenként is vissza tudd vonni azokat.
"""
)
