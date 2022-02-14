import { Organization } from '../types/Organization'

export const RESORTS: Organization[] = [
  {
    id: 'simonyi',
    name: 'Simonyi Károly Szakkollégium',
    shortDescription: 'Szakmában erős.',
    descriptionParagraphs: [
      `A Simonyi Károly Szakkollégium célja,
    hogy elsősorban a szakkollégium tagjai, lehetőség
    szerint a Villamosmérnöki és Informatikai Kar minden
    hallgatója számára lehetőséget biztosítson az egyetemi képzést kiegészítő ismeretek elsajátítására.`
    ],
    website: 'https://simonyi.bme.hu',
    logo: '/img/resorts/simonyi.svg',
    color: 'green',
    established: '2003.',
    email: 'info@simonyi.bme.hu',
    members: 180
  },
  {
    id: 'kszk',
    name: 'KSZK',
    shortDescription: 'Szakma. Barátok. Történelem.',
    descriptionParagraphs: [
      `A Kollégiumi Számítástechnikai Kör az egyetem legrégebben működő és legnagyobb aktív,
      informatikával foglalkozó öntevékeny csoportosulása, mely idén ünnepli 45+1. születésnapját.
      A KSZK egy vidám hangulatú, alkotó kedvű csapat, mely a Kar jó szakmai képességű, számítástechnika
      iránt kiemelten érdeklődő tagjaiból verbuválódott, és bővül évente új tehetségekkel, lelkes informatikusokkal,
      villamosmérnökökkel.`,
      `A KSZK a lehetőségek tárháza. A hely, ahol te, leendő mérnök, minden területen kipróbálhatod,
      továbbképezheted magad. Náluk kibontakoztathatod kreativitásod, tapasztalatot, mérnöki szemléletet
      szerezhetsz, miközben az úgynevezett soft skilljeidet is fejlesztheted. Ha számodra a szakma hivatás,
      ha szeretsz új dolgokat alkotni, vagy csak jó társaságra vágysz, a legjobb helyre kerültél. A reszort
      körei a szakma egy-egy meghatározó területével foglalkoznak a fejlesztés és üzemeltetés terén. A szakmai
      tevékenységen túl a KSZK-t pezsgő közösségi élet is jellemzi, melyet mi sem bizonyít jobban, mint az általuk
      szervezett rengeteg összejövetel: vacsorák, táborok, közös mozizások, paintballozás, LAN-partik, szakmai
      workshopok, sörözések és random pipázások. Újoncként te is részt vehetsz eseményeiken, ahol jó társaságban
      szerezhetsz gyakorlatban is hasznosítható tudást és sok új barátot. További információk és jelentkezés az
      ujonc.kszk.bme.hu oldalon.`
    ],
    // images: 'https://drive.google.com/drive/folders/1xoMzH5_pjYBGYwwIShrRkJeQp1Mu4meg?usp=sharing',
    website: 'https://kszk.bme.hu',
    application: 'https://ujonc.kszk.bme.hu',
    established: '1976',
    email: 'kszk@sch.bme.hu',
    members: 70,
    interests: ['Szolgáltatások', 'szerver üzemeltetés', 'SCHNet üzemeltetés', 'vik.wiki üzemeltetés/fejlesztés', 'számítógépes biztonság'],
    logo: '/img/communities/kszk.svg'
  },
  {
    id: 'szor',
    name: 'Szolgáltató Reszort (SZOR)',
    shortDescription: 'Nem kulturális jellegű szolgáltatások.',
    descriptionParagraphs: [
      `A 2004-ben alapított Szolgáltató Reszort azokat a köröket,
    csoportokat tömöríti magába, melyek valamilyen nem kulturális jellegű szolgáltatást
    nyújtanak a Schönherz Zoltán Kollégium lakóinak.
    Jelenleg 7 körünk szolgálja ki felváltva az éhesek kollégisták igényeit a hét szinte minden napján.
    Ők név szerint: Vödör Kör, Americano, PizzáSCH, Dzsájrosz, Palacsintázó, LángoSCH, Vörös Kakas Fogadó.`,
      `A kollégisták által rendelt ételeket a FoodEx "házhoz" is szállítja. Ha valaki baráti társaságával szívesen
    vizipipázna egyet, akkor érdemes a WTF-t keresnie. A kollégiumban található egy szauna is, melynek működtetésével
    a Szauna Kör foglalkozik. Ha valaki sokadmagával szeretne egy nagyot főzni, akkor az Edénykölcsönzőnél mindent megtalál,
    ami egy nagy lakomához kell.
    A már szimbólummá vált különböző foltokkal és villanykari pulóverekkel pedig a Pulcsi és Foltmékört érdemes keresni.`
    ],
    website: 'https://szor.sch.bme.hu',
    logo: 'https://logotar.schdesign.hu/preview/SZORReszort_preview.png'
  },
  {
    id: 'bulis',
    name: 'Bulis Reszort',
    shortDescription: 'A legjobb villanykaros bulik!',
    descriptionParagraphs: [
      `Csapatunk a BME Villamosmérnöki és Informatikai Karán működik,
      a Schönherz Kollégiumban, rendezvények széles palettájával várjuk a bulizni vágyó egyetemistákat.`
    ],
    website: 'https://bulis.sch.bme.hu',
    logo: '/img/resorts/bulis.svg'
  },
  {
    id: 'kultur',
    name: 'Kultúr Reszort',
    shortDescription: 'Kultúra és művészet.',
    descriptionParagraphs: [
      `A Kultúr Reszort a Schönherz kultúrával és művészettel foglalkozó köreit tömöríti egy nagy csoportba.
    A reszortban és a körökben lehetőség van a hobbidnak élni, olyanokat csinálni amik boldoggá tesznek. A reszortunk
    egy nagyon fontos célja, hogy a hallgatók, ne váljanak szakbarbárrá. A körök rendszeres rendezvénye a KultúrNight.`
    ],
    logo: '/img/resorts/kultur.svg'
  },
  {
    id: 'sport',
    name: 'Sport Reszort',
    shortDescription: 'Sportolni kívánó hallgatók reszortja.',
    descriptionParagraphs: [
      `A Sport reszort foglalja magába a Házban a sportért tenni kívánó embereket.
    A lehetőségek köre széles, rengeteg különböző sportban kipróbálhatják magukat a kollégisták
    vagy akár valami újat is behozhatnak a jelenlegiek közé.`
    ],
    website: 'https://dsk.sch.bme.hu/',
    logo: '/img/communities/dsk.png'
  },
  {
    id: 'kofer',
    name: 'Kollégiumi Felvételi és Érdekvédelmi Reszort',
    shortDescription: 'Érdekvédelem és felvételi.',
    descriptionParagraphs: [
      `A Kollégiumi Felvételi és Érdekvédelmi Reszort (KOFER) felel
    a kollégiumi felvételi zökkenőmentes lebonyolításáért illetve a kollégiumban
    élő szintközösségek életszínvonalának növeléséért, a szintközösségek képviseletéért.`
    ],
    logo: '/img/resorts/kofer.svg'
  },
  {
    id: 'schonherz',
    name: 'Schönherz',
    shortDescription: 'Kollégiumi közélet szolgálatában.',
    website: 'https://sch.bme.hu',
    logo: '/img/resorts/schonherz.svg'
  }
]
