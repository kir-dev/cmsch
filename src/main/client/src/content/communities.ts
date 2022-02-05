import { Community } from '../types/Organization'

export const COMMUNITIES: Community[] = [
  {
    id: 'kir-dev',
    name: 'Kir-Dev',
    hideName: true,
    shortDescription: 'A kollégium webfejlesztő köre.',
    descriptionParagraphs: [
      `A Kir-Dev a kollégium webfejlesztő csapata. Körünk egy összetartó baráti társaság, célja összefogni a hallgatóságnak azon tagjait,
      akik hasonló érdeklődésűek és inspirációt látnak a webfejlesztés világában. A kör teret ad a webes szakma felfedezésére, a
      tapasztalatok megosztására. Projektjeinket törekszünk a legfrissebb csapatszervezési és folyamatirányítási módszereknek megfelelően
      vezetni, hogy tagjaink minél közelebb kerülhessenek az iparban való munka megtapasztalásához, és jó alapokkal indulhassanak el.`,
      `Körünk fejleszti a PéK (Profil és Körök) alkalmazást – amely a Schönherz öntevékeny köreinek adminisztrációs rendszere –, ma már
      25 ezer feletti felhasználóval rendelkezik. Büszkélkedhetünk a SCH-Pincérrel, amelyen az utóbbi félévben több mint 3300 rendelés
      futott át, 42 nyitás került megrendezésre. A körünknél üzemel legutóbbi megbízásunk eredménye, a GólyaKörTe oldala, amely a
      “Speedrun munkacsoportunk” által került kivitelezésre nagyjából 2 hét alatt sok aktív tagunk nagyszerű erőbefektetésével. Hogy
      betekintést nyerhessetek további izgalmas projektjeink – például a TanulóSCH, Schörpong, vagy az InduláSch – futásába, látogassatok
      el weboldalunkra!`,
      `Tanfolyamainkon megadjuk neked a szükséges kezdőlöketet, amivel Te is belevetheted magad a webfejlesztés izgalmas és kihívásokkal
      teli világába. Lehetőséged van betekinteni az általunk használt Ruby on Rails, NodeJS és Spring Boot technológiáiba, a legkorszerűbb
      programozási nyelvekbe. Kellemes atmoszféra, gyakori szakmai poénok és segítőkész csapattagok várnak.`,
      `Ha érdeklődsz a kör iránt, akkor gyere el a tanfolyamainkra és gyűléseinkre!  A projektek vezetői szívesen bevezetnek mindenkit a
      fejlesztés folyamatába. Keress minket a honlapunkon található elérhetőségeink egyikén!`
    ],
    interests: ['szoftver', 'webfejlesztés', 'programnyelvek'],
    website: 'https://kir-dev.sch.bme.hu',
    facebook: 'https://fb.com/kirdevteam',
    instagram: 'https://instagr.am/kir.dev',
    application: 'https://kir-dev.sch.bme.hu/courses',
    established: '2001.',
    email: 'kir-dev@sch.bme.hu',
    members: 17,
    resortId: 'simonyi',
    logo: '/img/communities/kirdev.svg',
    darkLogo: '/img/communities/kirdev-white.svg',
    color: 'orange',
    images: ['https://kir-dev.sch.bme.hu/static/694736fc08b01fcbab76646a0b403c64/678ad/pek-next.webp'],
    videoIds: ['sY-s7O0FiYE', 'HA55hFBE32M'],
    searchKeywords: ['legjobb', 'web', 'programozás']
  },
  {
    id: 'schdesign',
    name: 'schdesign',
    hideName: true,
    shortDescription: 'Az schdesign a Simonyi Károly Szakkollégium kreatív alkotóműhelye.',
    descriptionParagraphs: `Körünk UI, UX, illetve digitális designnal foglalkozik.
    Célunk lehetőséget biztosítani, hogy a kódolás, forrasztás
    és szerver-konfigurálás mellett tagjaink a művészetet is be tudják emelni technológiai tudásuk mellé.`,
    interests: ['Grafika', 'Webdesign', '3D'],
    website: 'https://schdesign.hu',
    established: '2010.',
    email: 'hello@schdesign.hu',
    members: 54,
    resortId: 'simonyi',
    logo: '/img/communities/schdesign.svg',
    color: 'red'
  }
]
