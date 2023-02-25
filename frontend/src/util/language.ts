export function l<T extends keyof typeof languageData>(key: T, fields?: (typeof parameters)[T]) {
  let message = languageData[key]
  if (parameters[key]) {
    Object.entries(parameters[key] as Record<string, string>).forEach(([templateKey, value]) => {
      const field = fields?.[templateKey]
      message = message.replace(`{{${templateKey}}}`, field || value)
    })
  }
  return message
}

const languageData = {
  'error-boundary-title': 'Hiba történt!',
  'error-boundary-message': 'Sajnos ilyennel még nem találkoztunk. Légyszíves ezt jelezd a fejlesztőknek!',
  'not-found-message': 'Hoppá, úgy tűnik egy olyan oldalra kerültél, amely nem létezik többé!',
  'no-permission': 'Nincs jogosultságod megtekinteni!',
  'login-consent': 'Válassz bejelentkezési módot!',
  'login-helmet': 'Bejelentkezés',
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
  'riddle-incorrect-title': 'Helytelen válasz!',
  'riddle-incorrect-description': 'Próbáld meg újra, sikerülni fog!',
  'riddle-correct-title': 'Helyes válasz!',
  'riddle-correct-description': 'Csak így tovább!',
  'riddle-completed-title': 'Minden megvan!',
  'riddle-completed-description': 'Igazán szép munka, kolléga!',
  'riddle-completed-category-title': 'Minden megvan!',
  'riddle-completed-category-description': 'Igazán szép munka, kolléga!',
  'riddle-submission-failed': 'Nem sikerült beadni a Riddle-t.',
  'task-empty-title': 'Üres megoldás',
  'task-empty-description': 'Üres megoldást nem küldhetsz be.',
  'task-too-large-title': 'Túl nagy a fájl',
  'task-too-large-description': 'A feltöltött fájl túllépte a 30 MB-os feltöltési korlátot!',
  'task-not-found-title': 'Feladat nem található',
  'task-not-found-description': 'Ilyen feladat nem létezik vagy nincs jogosultságod hozzá.',
  'task-category-failed': 'Nem sikerült lekérni ezt a feladat kategóriát',
  'token-completed': 'Ahol eddig jártál',
  'token-empty': 'Még nem szereztél pecsétet',
  'token-scan-network-error': 'Hálózati hiba a token érvényesítésénél',
  'token-scan-read-error': 'Beolvasási hiba.',
  'location-query-failed': 'A pozíciók nem érhetőek el.',
  'location-sensor-denied': 'Helymeghatározás nem elérhető',
  'users-location-title': 'A te pozíciód',
  'component-unavailable': 'Ez a komponens nem elérhető.',
  'result-query-failed': 'Nem sikerült lekérni az eredményeket.',
  'alias-change-successful': 'Becenév sikeresen módosítva',
  'alias-change-failure': 'Nem sikerült megváltztatni a becenevet',
  'alias-change-not-allowed': 'A becenév szerkesztése nem egedélyezett!',
  'component-unavaliable': 'Komponens nem elérhető!',
  'organization-title': 'Reszortok',
  'organization-description': 'Az egyes reszortok a hasonló jellegű köröket összefogó szervezetek.',
  'community-title': 'Körök',
  'community-description':
    'A karon számtalan öntevékeny kör működik, mindenki megtalálhatja az' +
    'érdeklődési körének megfelelő csoportot. A körök a Schönherz Kollégiumban működnek.',
  'page-load-failed': '{{title}} betöltése sikertelen!',
  'page-load-failed-contact-developers': '{{title}} betöltése sikertelen!\n Keresd az oldal fejlesztőit.'
}

const parameters: Partial<Record<keyof typeof languageData, Record<string, string | undefined>>> = {
  'page-load-failed': { title: 'Oldal' },
  'page-load-failed-contact-developers': { title: 'Oldal' }
}
