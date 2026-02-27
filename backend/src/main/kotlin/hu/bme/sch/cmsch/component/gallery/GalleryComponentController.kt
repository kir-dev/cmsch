package hu.bme.sch.cmsch.component.gallery

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_GALLERY
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/gallery")
@ConditionalOnBean(GalleryComponent::class)
class GalleryComponentController(
    adminMenuService: AdminMenuService,
    component: GalleryComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    GalleryComponent::class.java,
    component,
    PERMISSION_CONTROL_GALLERY,
    "Galéria",
    "Galéria testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMarkdown = """
A **Galéria** komponens a rendezvényen készült fotók és képek feltöltését és megjelenítését teszi lehetővé.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod a galéria alapvető adatait:

- **Lap címe** – a böngésző címsorában megjelenő szöveg.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – mely szerepkörökkel érhető el a galéria oldala.

## Galéria kezelése

A **Galéria** menüpont alatt töltheted fel a képeket:

- **Új kép feltöltése** – új fotó rögzítése.
- **Szerkesztés / Törlés** – képek adatainak módosítása vagy eltávolítása.

## Kép feltöltése / szerkesztése

- **Cím** – a kép neve vagy rövid leírása.
- **Kép** – a fájl feltöltése.
- **Látható** – ha be van kapcsolva, megjelenik a galériában.
- **Kezdőlapra mehet** – ha be van kapcsolva, a kép megjelenhet a kezdőlapi carouselben is (ha a kezdőlap komponensnél ez engedélyezve van).

## Használati tippek

- Érdemes jó minőségű, de optimalizált méretű képeket feltölteni a gyors betöltés érdekében.
- A **Kezdőlapra mehet** opcióval könnyen kiemelheted a rendezvény legjobb pillanatait a nyitóoldalon.
"""
)
