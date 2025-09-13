Az **Események** komponens segítségével a rendezvény/program összes eseményét rögzítheted és megjelenítheted a felhasználók számára.  
Az adminfelületen keresztül kezelheted az eseményeket és testre szabhatod a megjelenést.

## Beállítások

A **Komponens beállításai** menüpontban konfigurálhatod az események oldalát:

- **Lap címe** – ez jelenik meg a böngésző címsorában.
- **Menü neve** – a menüben látható név.
- **Jogosultságok** – minimum szerepkör, amelynek látható az oldal (pl. mindenki, belépett felhasználók, szervezők).
- **Részletes nézet** – ha be van kapcsolva, az események külön oldalon részletesen is megnyithatók.
- **Keresés elérhető** – bekapcsolva megjelenik egy kereső az események oldal tetején.
- **Csoportosítás naponként** – a programok napokra bontva jelennek meg.
- **Kategória / helyszín / nap szerinti szűrés** – beállítható, hogy a felhasználók tudjanak szűrni ezen mezők alapján.
- **Oldal tetején megjelenő szöveg** – általános leírás a programokról, markdown formátumban.

## Események kezelése

Az **Események** menüpont alatt a rendezvény teljes programlistáját kezelheted:

- **Új esemény létrehozása** – új program rögzítése.
- **Szerkesztés** – meglévő esemény adatainak módosítása.
- **Törlés** – felesleges események eltávolítása.
- **Importálás / Exportálás** – események tömeges betöltése vagy kimentése.

A lista tartalmazza az összes rögzített eseményt, státuszukkal együtt (látható / időpont / helyszín).

## Esemény létrehozása / szerkesztése

Új esemény felvételekor vagy szerkesztésekor a következő főbb mezőket kell megadnod:

- **Cím** – az esemény neve.
- **Url** – rövid azonosító az esemény webcíméhez (csak kisbetűk és kötőjelek). A frontenden ${appComponent.siteUrl}event/{url} linken érhető el.
  Példa: `nyitoceremonia`
- **Leírás** – részletes szöveg az eseményről (markdown formátumban).
- **Helyszín** – ahol az esemény zajlik.
- **Időpont (kezdés és befejezés)** – az esemény pontos időtartama.
- **Látható** – ha be van kapcsolva, megjelenik a felhasználói oldalon.
- **Minimum rang a megtekintéshez** – korlátozhatod, hogy kik lássák (pl. csak résztvevők, csak szervezők).
- **Kategória** – opcionális besorolás, ami a szűréshez is használható.
- **Kép / illusztráció** – vizuális elem az eseményhez.
- **OG:Title, OG:Image, OG:Description** – közösségi megosztásokhoz tartozó metaadatok. ${appComponent.adminSiteUrl}share/event/{url} linkkel tudod megosztani a híreket, hogy megjelenjenek megfelelően.

## Használati tippek

- A **Részletes nézet** bekapcsolásával minden esemény külön oldalt kap részletes leírással.
- Ha **szűrési lehetőségeket** is bekapcsolsz (nap, helyszín, kategória), a felhasználók könnyebben navigálhatnak a programok között.
- Az **importálás** hasznos nagy mennyiségű program gyors rögzítésére (pl. Excelből).
- Az **időpontok** helyes beállítása kulcsfontosságú a felhasználói megjelenítéshez és az aktuális program kiemeléséhez.