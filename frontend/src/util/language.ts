export const l = (key: keyof typeof languageData) => {
  return languageData[key]
}

const languageData = {
  'error-boundary-title': 'Hiba történt!',
  'error-boundary-message': 'Sajnos ilyennel még nem találkoztunk. Légyszíves ezt jelezd a fejlesztőknek!',
  'not-found-message': 'Hoppá, úgy tűnik egy olyan oldalra kerültél, amely nem létezik többé!',
  'no-permission': 'Nincs jogosultságod megtekinteni!',
  'login-expired-title': 'Bejelentkezés lejárt',
  'login-expired-description': 'Hiba esett a bejelentkezési folyamatba!',
  'login-consent': 'Válassz bejelentkezési módot!',
  'logout-title': 'Kijelentkezés',
  'logout-description': 'Sikeres kijelentkezés!',
  'error-page-helmet': 'Hiba',
  'error-page-title': 'Hiba történt',
  'error-connection-unsuccessful': 'Kapcsolódás sikertelen',
  'unauthorized-page-helmet': 'Nem vagy bejelentkezve',
  'unauthorized-page-title': 'Bejelentkezés szükséges',
  'unauthorized-page-description': 'Az oldal eléréséhez be kell jelentkezned!',
  'toast-title-success': 'Siker',
  'toast-title-error': 'Hiba',
  'toast-title-warning': 'Figyelmeztetés',
  'toast-title-info': 'Információ',
  'cookie-consent-title': 'Fogadd el cookie-jainkat!',
  'cookie-consent-description':
    'Ezen az oldalon cookie-kat használunk a megfelelő működés érdekében. A weboldal használatával ebbe beleegyezel.',
  'event-load-failed': 'Esemény betöltése sikertelen!\n',
  'event-load-failed-contact-developers': 'Esemény betöltése sikertelen!\n Keresd az oldal fejlesztőit.',
  'article-load-failed': 'Oldal betöltése sikertelen!\n',
  'article-load-failed-contact-developers': 'Űrlap betöltése sikertelen!\n Keresd az oldal fejlesztőit.',
  'news-item-load-failed': 'Hír betöltése sikertelen!\n',
  'news-item-load-failed-contact-developers': 'Hír betöltése sikertelen!\n Keresd az oldal fejlesztőit.',
  'news-list-load-failed': 'Hírek betöltése sikertelen!\n',
  'news-list-load-failed-contact-developers': 'Hírek betöltése sikertelen!\n Keresd az oldal fejlesztőit.',
  'form-load-failed': 'Oldal betöltése sikertelen!',
  'form-load-failed-contact-developers': 'Űrlap betöltése sikertelen!\n Keresd az oldal fejlesztőit.',
  'event-list-message': 'A változás jogát fenntartjuk! Kísérd figyelemmel az oldal tetején megjelenő értesítéseket!',
  'riddle-incorrect-title': 'Helytelen válasz!',
  'riddle-incorrect-description': 'Próbáld meg újra, sikerülni fog!',
  'riddle-correct-title': 'Helyes válasz!',
  'riddle-correct-description': 'Csak így tovább!',
  'riddle-completed-title': 'Minden megvan!',
  'riddle-completed-description': 'Igazán szép munka, kolléga!',
  'riddle-completed-category-title': 'Minden megvan!',
  'riddle-completed-category-description': 'Igazán szép munka, kolléga!',
  'task-empty-title': 'Üres megoldás',
  'task-empty-description': 'Üres megoldást nem küldhetsz be.',
  'task-too-large-title': 'Túl nagy a fájl',
  'task-too-large-description': 'A feltöltött fájl túllépte a 30 MB-os feltöltési korlátot!',
  'task-not-found-title': 'Feladat nem található',
  'task-not-found-description': 'Ilyen feladat nem létezik vagy nincs jogosultságod hozzá.',
  'task-category-failed': 'Nem sikerült lekérni ezt a feladat kategóriát',
  'task-list-failed': 'Nem sikerült lekérni a feladatokat',
  'token-request-failed': 'Nem sikerült lekérni a QR kódokat.',
  'token-helmet': 'QR pecsétek',
  'token-heading': 'QR kód pecsétek',
  'token-message':
    'A standoknál végzett aktív tevékenyégedért QR kódokat lehet beolvasni. Ha eleget gyűjtesz össze, beválthatod egy tanköri jelenlétre.',
  'token-completed': 'Ahol eddig jártál',
  'token-empty': 'Még nem szereztél pecsétet',
  'token-scan-helmet': 'QR beolvasás',
  'token-scan-consent': 'Szkenneld be a QR kódot!',
  'token-scan-network-error': 'Hálózati hiba a token érvényesítésénél',
  'token-scan-read-error': 'Beolvasási hiba.',
  'profile-load-failed': 'Profil betöltése sikertelen!\n',
  'profile-load-failed-contact-developers': 'Profil betöltése sikertelen!\nKeresse az oldal fejlesztőit a hiba kinyomozása érdekében!',
  'location-query-failed': 'A pozíciók nem érhetőek el.',
  'location-sensor-denied': 'Helymeghatározás nem elérhető',
  'users-location-title': 'A te pozíciód'
}
