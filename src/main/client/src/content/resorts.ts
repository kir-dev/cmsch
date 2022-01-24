import { Organization } from '../types/Organization'

export const RESORTS: Organization[] = [
  {
    id: 'simonyi',
    name: 'Simonyi Károly Szakkollégium',
    shortDescription: 'Szakmában erős.',
    description: `A Simonyi Károly Szakkollégium célja,
    hogy elsősorban a szakkollégium tagjai, lehetőség
    szerint a Villamosmérnöki és Informatikai Kar minden
    hallgatója számára lehetőséget biztosítson az egyetemi képzést kiegészítő ismeretek elsajátítására.`,
    website: 'https://simonyi.bme.hu',
    logo: '/img/resorts/simonyi.svg',
    color: 'green',
    established: '2003.',
    email: 'info@simonyi.bme.hu',
    members: 180
  },
  {
    id: 'kszk',
    name: 'Kollégiumi Számítástechnikai Kör',
    shortDescription: 'Szakma. Barátok. Történelem.',
    description: `A Kollégiumi Számítástechnikai Kör a kar egyik első – jelenleg is aktív – öntevékeny csoportosulása,
    mely 1976-ban került megalapításra. Tagjaink az informatika különböző területei iránt kiemelten érdeklődő öntevékeny
    egyetemista hallgatók, akik tanulmányaikon túl, szabaidejükben fejlesztik magukat és rendszereinket, megkönnyítve ezzel
    a kollégisták, valamint a kar hallgatóinak hétköznapjait. Aktív tagjaink az iparban alkalmazott naprakész tudásra tesznek
    szert itt, kísérleteznek saját ötleteikkel és jó kapcsolatokat építenek ki ebben a jó hangulatú, alkotó kedvű közösségben.
    A KSZK tevékenysége két nagy területre bontható: az üzemeltetésre és a fejlesztésre. Előbbi esetén hálózattervezéssel,
    felügyelettel, monitorozással és különböző típusú szerverszolgáltatások üzemeltetésével foglalkozunk. Utóbbi esetén
    pedig a web-, mobil- és szoftverfejlesztés jön szóba, valamint tevékenységeink során az IT security-vel is behatóan
    foglalkozunk.`,
    website: 'https://kszk.sch.bme.hu',
    logo: '/img/resorts/kszk.svg'
  },
  {
    id: 'szor',
    name: 'Szolgáltató Reszort (SZOR)',
    shortDescription: 'Nem kulturális jellegű szolgáltatások.',
    description: `A 2004-ben alapított Szolgáltató Reszort azokat a köröket,
    csoportokat tömöríti magába, melyek valamilyen nem kulturális jellegű szolgáltatást
    nyújtanak a Schönherz Zoltán Kollégium lakóinak.
    Jelenleg 7 körünk szolgálja ki felváltva az éhesek kollégisták igényeit a hét szinte minden napján.
    Ők név szerint: Vödör Kör, Americano, PizzáSCH, Dzsájrosz, Palacsintázó, LángoSCH, Vörös Kakas Fogadó.
    A kollégisták által rendelt ételeket a FoodEx "házhoz" is szállítja. Ha valaki baráti társaságával szívesen
    vizipipázna egyet, akkor érdemes a WTF-t keresnie. A kollégiumban található egy szauna is, melynek működtetésével
    a Szauna Kör foglalkozik. Ha valaki sokadmagával szeretne egy nagyot főzni, akkor az Edénykölcsönzőnél mindent megtalál,
    ami egy nagy lakomához kell.
    A már szimbólummá vált különböző foltokkal és villanykari pulóverekkel pedig a Pulcsi és Foltmékört érdemes keresni.`,
    website: 'https://szor.sch.bme.hu',
    logo: 'https://logotar.schdesign.hu/preview/SZORReszort_preview.png'
  },
  {
    id: 'bulis',
    name: 'Bulis Reszort',
    shortDescription: 'A legjobb villanykaros bulik!',
    description: `Csapatunk a BME Villamosmérnöki és Informatikai Karán működik,
      a Schönherz Kollégiumban, rendezvények széles palettájával várjuk a bulizni vágyó egyetemistákat.`,
    website: 'https://bulis.sch.bme.hu',
    logo: '/img/resorts/bulis.svg'
  },
  {
    id: 'kultur',
    name: 'Kultúr Reszort',
    shortDescription: 'Kultúra és művészet.',
    description: `A Kultúr Reszort a Schönherz kultúrával és művészettel foglalkozó köreit tömöríti egy nagy csoportba.
    A reszortban és a körökben lehetőség van a hobbidnak élni, olyanokat csinálni amik boldoggá tesznek. A reszortunk
    egy nagyon fontos célja, hogy a hallgatók, ne váljanak szakbarbárrá. A körök rendszeres rendezvénye a KultúrNight.`,
    logo: '/img/resorts/kultur.svg'
  },
  {
    id: 'sport',
    name: 'Sport Reszort',
    shortDescription: 'Sportolni kívánó hallgatók reszortja.',
    description: `A Sport reszort foglalja magába a Házban a sportért tenni kívánó embereket.
    A lehetőségek köre széles, rengeteg különböző sportban kipróbálhatják magukat a kollégisták
    vagy akár valami újat is behozhatnak a jelenlegiek közé.`,
    website: 'https://dsk.sch.bme.hu/',
    logo: '/img/communities/dsk.png'
  },
  {
    id: 'kofer',
    name: 'Kollégiumi Felvételi és Érdekvédelmi Reszort',
    shortDescription: 'Érdekvédelem és felvételi.',
    description: `A Kollégiumi Felvételi és Érdekvédelmi Reszort (KOFER) felel
    a kollégiumi felvételi zökkenőmentes lebonyolításáért illetve a kollégiumban
    élő szintközösségek életszínvonalának növeléséért, a szintközösségek képviseletéért.`,
    logo: '/img/resorts/kofer.svg'
  },
  {
    id: 'sssl',
    name: 'Szent Schönherz Senior Lovagrend',
    shortDescription: 'Elsőéves hallgatók szolgálatában.',
    description: `A Szent Schönherz Senior Lovagrend (SSSL) főleg a Karra
    kerülő elsőéves hallgatók egyetemi és közösségi beilleszkedésével foglalkozik.
    Ezen felül segítséget nyújt a kollégium más szervezeteinek az utánpótlás keresésben,
    ezen szervezeteknek az elsősökkel kapcsolatos döntéseik előkészítésében, meghozatalában.
    Legtöbb rendezvényünk első évesekkel kapcsolatos, pl. a Gólyatábor, Gólyahét, Gólyabál.`,
    website: 'https://sssl.sch.bme.hu'
  }
]
