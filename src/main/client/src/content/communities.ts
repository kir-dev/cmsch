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
    // imageIds: ['https://kir-dev.sch.bme.hu/static/694736fc08b01fcbab76646a0b403c64/678ad/pek-next.webp'],
    // videoIds: ['sY-s7O0FiYE', 'HA55hFBE32M'],
    searchKeywords: ['legjobb', 'web', 'programozás']
  },
  {
    id: 'ac',
    name: 'AC Studio & Live',
    descriptionParagraphs: [
      `Az AC Studio & Live rendezvénytechnikával, elő hang- és fénytechnikával,
    valamint stúdiótechikával is foglalkozik. Mi hangosítjuk az összes kollégiumi rendezvényt,
    a kisebb egy mikrofonos rendezvényektől kezdve egészen a koncertekig. A legtöbb QPA indulót
    mi szoktuk felvenni, melyeket utómunkázunk is, valamint kisebb-nagyobb zenekarok is meg szoktak
    fordulni a stúdiónkban. A fénytechnikai profilunk is erős, egészen komoly eszközparkkal rendelkezünk,
    melyet ki is szoktunk használni DJ-s bulikon és koncerteken. Ha a fentiek közül
    bármelyik is érdekel, akkor nálunk a helyed!`
    ],
    // imageIds: [
    //   '1hXrMKKFOOnm2AqRJSPmNRKzWLJLTGBoq',
    //   '1sdcfII-YOSwqGVXm087QTypnAAHQuEc8',
    //   '1A1vB3Z0YbdKVbpEpeWP8NO7mHpZ_RQcH',
    //   '16PSRo7QI9Q9EIKcEi49dS06KQFd0t-aV',
    //   '1r9IK70s5dqIkS0uicZ-wgt9XsmBKi5du',
    //   '1Sk2nMZg2rxuTj8OpFmkff_lX4hKuuzIq',
    //   '1Z49XOAg7wB3joEEs4sfQpwwjRsueQD-c',
    //   '1GlPbVhcOB4xQI77gJ9UxBsUTJ8VfDnRj'
    // ],
    // videoIds: ['1CT5t9oOoUBcHeGtD09oA-v1WB7_yB7oi'],
    website: 'https://acstudio.sch.bme.hu/',
    established: '1993',
    email: 'ac-info@simonyi.bme.hu',
    interests: ['hangtechnika', 'fénytechnika', 'stúdiótechnika'],
    resortId: 'simonyi',
    logo: '/img/communities/ac.svg'
  },
  {
    id: 'americano',
    name: 'Americano',
    descriptionParagraphs: [
      `Jó hangulatú csapatunkkal a szorgalmi időszakban
    minden kedden finomabbnál-finomabb hamburgerekkel kényesztetjük a kollégistákat.
    Az állandó kínálatunk mellett rendszeresen tartunk speciális nyitásokat, ahol hambi
    különlegességeket készítünk. Csapatunkat kreativitás, összetartás és lelkesedés jellemzi.
    Ha szereted a minőségi hamburgereket és szeretnéd, hogy az álom hambidat tömegek
    ismerjék, akkor jelentkezz az Americanoba!`
    ],
    // videoIds: ['1QC34LIpbK_xjRRnaHE5ODTa1WIQaTqdX'],
    website: 'https://schpincer.sch.bme.hu/americano',
    established: '2011',
    email: 'americanokor@gmail.com',
    interests: ['hamburger készítés'],
    resortId: 'szor',
    logo: '/img/communities/americano.svg'
  },
  {
    id: 'bbk',
    name: 'Bor Baráti Kör',
    descriptionParagraphs: [
      `A BBK a borkultúra iránt érdeklődést mutató egyetemisták
    közössége. Célunk a kultúrált borfogyasztás népszerűsítése a korosztályunkban.
    Szeretnénk, ha egyre több érdeklődő ismerné meg a tudatos borfogyasztás mögött rejlő szépséget.
    Klubszobánkban heti rendszerességgel tartunk kötetlen, -névhez híven- baráti hangulatú borkostolókat
    kifejezetten az egyetemisták pénztárcájára szabva. Ezek a rendezvények lehetőséget adnak arra,
    hogy a vendégek és a körtagok a borok szakszerű megkóstolása és véleményezése által mélyíthessék
    el a témával kapcsolatos ismereteiket. Mindeközben -közösségi esemény révén- az ismerkedésre és
    barátok szerzésére is lehetőség nyílik.`,
      `Rendszeresen hívunk meg borászokat a kollégiumba, akik
    egy előadással egybekötött borkóstolón keresztül mutatják be saját boraikat, mesélik el az elkészítésük
    körülményeit, és az esetleg hozzájuk tartozó elveket, filozófiát. Félévente egy-egy párlatkostolót is
    tartunk, ahol egy párlatszakértő osztja meg velünk a témával kapcsolatos gondolatait. A borral és borászattal
    kapcsolatos tudásra, illetve annak megszerzésére nagy hangsúlyt fektetünk, mindezt tanfolyamokon keresztül
    igyekszünk átadni. Évente kirándulásokat is szervezünk, melyek során alkalmanként bejárjuk Magyarország egyik
    borvidékét, ahol több helyi borász is vendégül lát minket, íly módon ápolva a meglévő, és építve az új kapcsolatokat.
    Ha unod már a szobatársaidat, és túl sok az egyetem miatti stressz, akkor ülj be hozzánk, vegyél el egy poharat,
    és kóstolj meg pár bort!`
    ],
    website: 'https://www.facebook.com/BorBaratiKor',
    established: '2001',
    email: 'schbbk@gmail.com',
    members: 15,
    interests: ['bor'],
    resortId: 'kultur',
    logo: '/img/communities/bbk.svg'
  },
  {
    id: 'bss',
    name: 'BSS',
    color: 'blue',
    descriptionParagraphs: [
      `Mi vagyunk azok, akik megörökítjük az első egyetemi élményeiteket,
    élőben közvetítjük a diplomaosztótokat, és már Orosz László Tanár Urat is elhívtuk az élő műsorunkba.`,
      `A Budavári Schönherz Stúdió Magyarország legnagyobb, kizárólag egyetemisták által működtetett tévéstúdiója.
      60 éves működésünk alatt számtalan tagunk jutott el a stúdiónk falai közül a televíziós iparágba, és hivatásos
      szinten űzi a szakmát. Jelenleg 40 fős aktív tagságunkkal minden félévben megörökítjük a Schönherz, illetve
      a Kar fontos eseményeit, emellett pedig saját projekteken is dolgozunk.`,
      `A televíziós műsorkészítéstől az élő közvetítések világáig, a videóvágáson és
      az operatőri folyamatokon át, a krimpelőfogó helyes használatáig számtalan területen tevékenykedünk.`,
      `Ha fognál valami igazán nagyot a kezedben, és minden álmod, hogy az egy tévés kamera legyen, akkor várunk Téged is!`
    ],
    // imageIds: 'https://drive.google.com/open?id=1OeWFasde1C46GprP_RUyW91hs8mWwLV5',
    // videoIds: 'https://drive.google.com/file/d/1NisXkGOasXq9mwtJjIRuNq3cmZFOcKbm/view?usp=sharing',
    website: 'https://bsstudio.hu/',
    application: 'https://bsstudio.hu/tanfolyamok',
    established: '1962',
    email: 'bssinfo@sch.bme.hu',
    members: 40,
    interests: ['tévéstúdió', 'közvetítések', 'tartalom-előállítás'],
    resortId: 'simonyi',
    logo: '/img/communities/bss.svg'
  },
  {
    id: 'buvesz',
    name: 'Bűvész kör',
    descriptionParagraphs: [
      `Célunk, hogy összefogjuk a bűvészet iránt érdeklődőket.
    A gyűléseink során bemutatjuk egymásnak legújabb trükkjeinket, és hasznos tanácsokkal
    látjuk el egymást a fejlődés érdekében. Kollégiumi rendezvényeken is találkozhattok velünk,
    ahol rendszeresen fellépünk. Van oktatóanyagunk amiket edukációs célra tudunk használni így
    garantált a fejlődés! Valamint van néhány közös kellék amiket rendezvényeken lehet használni.`
    ],
    // imageIds: 'https://drive.google.com/open?id=1965jlIvQzvlWgP_FbAjeYkatEE-jkAGW',
    // videoIds: 'https://drive.google.com/file/d/1oATL9BApD9fR507GMa4YNConWlOm8qtw/view?usp=sharing',
    application: 'https://forms.gle/7kNGvcgWXuM5TaX6A',
    email: 'buvesz@sch.bme.hu',
    interests: ['mágia'],
    resortId: 'kultur',
    logo: '/img/communities/buvesz.svg'
  },
  {
    id: 'devteam',
    name: 'Devteam',
    descriptionParagraphs: [
      `A DevTeam a KSZK fejlesztő köre. Az informatika számos területével foglalkozunk,
      többek között például alkalmazás- és mobilszoftver fejlesztéssel, weblapkészítéssel
      és játékfejlesztéssel. Ezen felül találkozhatsz mikrokontrollerek programozásával,
      legyen az akár Raspberry Pi vagy Arduino. Saját ötleteid megvalósításában is szívesen
      nyújtunk segítő kezet, illetve egyes gyűléseinken gyorstalpalókat tartunk, hogy az
      érdeklődők mielőbb be tudjanak csatlakozni a munkába.`,
      `Szeretettel várunk mindenkit, legyen akár profi fejlesztő, vagy olyan,
      aki csak most ismerkedik a fejlesztés szépségeivel – nálunk mindenki talál
      a képességeinek megfelelő elfoglaltságot.`
    ],
    resortId: 'kszk',
    logo: '/img/communities/devteam.svg'
  },
  {
    id: 'dezso',
    name: 'Dezső Buli',
    established: '1992',
    descriptionParagraphs: [
      `Kevés ember gondol az otthoni disznóvágásra, amikor elköltözik Buda szívébe –
      a Dezső Buli feladata azonban pont az, hogy ezt az élményt a lehető legjobban
      visszaadja. Egyrészt tradíció, másrészt remek szórakozás az egész napon átívelő
      sürgés-forgás a kollégiumban. A hagyományos esti disznótor élő cigányzenével pedig
      annyira megindító, hogy torok nem marad szárazon. Félévente csak egy alkalom, de az biztosan emlékezetes.`,
      `Robinson védjegyével 1992 óta töretlenül:`,
      `„Dezsőnek márpedig meg kell halnia!”`,
      `Körvezető: Stahorszki Márton`
    ],
    resortId: 'bulis',
    logo: '/img/communities/dezso.svg'
  },
  {
    id: 'dsk',
    name: 'DSK',
    descriptionParagraphs: [
      `A Diák Sport Kör alapvetően a kollégistáknak nyújt lehetőséget,
    hogy aktívan tudjanak kikapcsolódni. Ezt a Schönherzben 1119-ben található pingpong terem
    a és 2.emeleten lévő boxterem a  üzemeltetésével tesszük lehetővé, amik egész évben várják
    a sportolni vágyókat. Valamint sportos rendezvényeinkkel színesítjük a közéletet, ilyenkor
    télen tartjuk a Jeges Estet, ahol egy jót korizunk együtt, tavasszal egy evezős napot egy
    kis víz melletti kikapcsolódás a Kopaszi-gátnál, valamint különböző bajnokságokat is szervezünk
    az asztalitenisztől kezdve a streetball-ig. A sok mozgás mellett a bulizást sem vetjük meg,
    egy schönherzes partyhoz kiváló ráhangolódást biztosít a Toronyfutás, amivel már találkozhattatok
    az ősz félévben. Még egy állandó tevékenységünk közé tartozik a szertár üzemeltetése is többek
    között akár sátrakat, snowboardot, korcsolyát és mindenféle sportfelszerelést is lehet kölcsönözni.
    Ha szereted a sportokat, vagy csak szeretnéd kipróbálni magad a korábban említett rendezvények
    megszervezésében, akkor csatlakozz bátran társaságunkba.`
    ],
    // videoIds: 'https://drive.google.com/file/d/141xwetfs58ON3PEXuKLPSy3zrKabSSG7/view?usp=sharing',
    website: 'https://dsk.sch.bme.hu/',
    application: 'https://docs.google.com/forms/d/1KqZhnrbr9SPg6B-XKVVJWFv7g7N-swSNbKWPtXFa1gE',
    established: '2002',
    email: 'dsk@sch.bme.hu',
    interests: ['sportesemények'],
    resortId: 'sport',
    logo: '/img/communities/dsk.png'
  },
  {
    id: 'dzsajrosz',
    name: 'Dzsájrosz',
    descriptionParagraphs: [
      `Minden csütörtök este ínycsiklandó husik,
    mennyei házi szószok és különféle zöldségek hadának segítségével
    látjuk el minél jobb és minél bőségesebb gyrosszal a kollégium
    lakóit. Több éves tapasztalattal és folyamatos újításokkal azon
    igyekszünk, hogy a kolisoknak még a sarki gyrososokhoz se kelljen
    lemenni, ha a környék legjobbját szeretnék megkóstolni. Az ajtónk
    nyitva áll! Mindenkit szívesen látunk, aki megtanulná a gyros
    készítésének apró fortélyait, örömmel részese lenne egy jó és
    összetartó csapatnak, illetve időről időre szívesen lejönne a
    konyhába, hogy kiszakadva a hétköznapokból, jó hangulatban, fincsi
    gyrosokkal elégítse ki a kolisok kulináris vágyait.`
    ],
    // imageIds: 'https://drive.google.com/open?id=1uTixkj61Vnl4c8RGNSoG80KbOE3ZuUbF',
    // videoIds: 'https://drive.google.com/file/d/1CGNRKr1RCEYwF1Yxw4BXcAJwhsdPpE-c/view?usp=sharing',
    website: 'https://dzsajrosz.sch.bme.hu/',
    application: 'https://docs.google.com/forms/d/e/1FAIpQLSfQ6Xj5VgEc0mlwHPfLH6hRt5GseUD45OC81iZjS0tZ7QUoQg/viewform?usp=sf_link',
    established: '2015',
    email: 'dzsajrosz@sch.bme.hu',
    interests: ['gyroskészítés'],
    resortId: 'szor',
    logo: '/img/communities/dzsajrosz.svg'
  },
  {
    id: 'foodex',
    name: 'FoodEx',
    descriptionParagraphs: [
      `Mi vagyunk azok, akik miatt még melegen megkapod a kajádat, megkímélünk a hosszas sorbanállástól,
      a kényelmetlen liftezésektől. Hétfőnként a Vödör sültkrumpliját, keddenként az Americano által
      készített hot-dogokat és hamburgereket, szerdánként a PizzáSCH ínycsiklandozó pizzáit, csütörtökönként
      a Dzsájrosz gyrosait, vasárnaponként a Lángosch lángosait szállítjuk ki.`,
      `Ha szeretnél egy állandóan mozgásban lévő csapat tagja lenni és ingyen kaját enni minden műszakod végén,
      vagy csak úgy érzed, jót tenne egy kis lépcsőzés, akkor mindenképpen nálunk a helyed!`
    ],
    // imageIds: 'https://drive.google.com/open?id=1-ia0PnaN2ti4zGN-YZQWYRdLJrmf3ln5',
    website: 'https://foodex.sch.bme.hu/',
    application: 'https://forms.gle/gwHi4tZ8FynYjkSs8',
    established: '2003',
    email: 'foodex@sch.bme.hu',
    resortId: 'szor',
    logo: '/img/communities/foodex.svg'
  },
  {
    id: 'ha5kfu',
    name: 'HA5KFU',
    color: 'blue',
    descriptionParagraphs: [
      `A HA5KFU (“Kafu”) a kollégium 19. emeletén található rádióamatőr klub. Megismerheted
      a rádióamatőrködés világát, rövidhullámon összeköttetést létesíthetsz a világ bármelyik
      részével (a tetőn lévő hatalmas antennák segítségével), műholdak adását foghatod, vagy
      akár meteorokat is számolhatsz egy számítógép és egy digitális-tévé vevő segítségével!
      Mi biztosítjuk a rádiós összeköttetést villanykaros rendezvényeken is, ehhez az eszközöket
      folyamatosan fejlesztjük, karbantartjuk.`,
      `Közösségünk egy összetartó csapat, ahova bátran csatlakozhatnak villanyosok, infósok,
      üzemmérnökök is. Akár saját rádiós projektet hozol a klubba, akár a jelenleg futó
      projektekbe csatlakoznál, a körtagok támogatásával könnyedén belevághatsz! Nálunk
      megtanulhatsz analóg áramköröket tervezni és készíteni, mikrokontrollert programozni
      valamint szoftverből rádiót írni, amiket terepen is kipróbálhatsz egy rádióklubos
      kirándulás vagy tábor alkalmával. Segítünk felkészülni az NMHH rádióamatőr vizsgájára,
      ami után akár a klub 800 wattos végfokerősítőjét használva önállóan forgalmazhatsz amatőr
      frekvenciasávokon. Tanfolyamunk minden félév elején indul, és nem igényel előismereteket!`,
      `Ízelítő az aktuális projektekből: kereszt-Yagi antenna és antennaforgató műholdazáshoz,
      FPGA vevő rövidhullámra, SSTV-SSDV átjátszóállomás, HamNet vezeték nélküli internet hálózat és még sok más.`
    ],
    // imageIds: 'https://drive.google.com/drive/folders/1PNF6eOaIRk86nrcp3VAT5I5rZAX4Nrsq?usp=sharing',
    // videoIds: 'https://drive.google.com/file/d/1V_7RYcnd_BfhkihuvLI_UtS8ComPKmC0/view?usp=sharing',
    website: 'https://ha5kfu.hu/',
    application: 'https://forms.gle/AjPcphBbdoWHejcP7',
    established: '1954',
    email: 'ha5kfu@simonyi.bme.hu',
    members: 16,
    interests: ['rádióamatőr tevékenység', 'műhold vétel', 'rádiózás', 'antenna építés', 'rádió építés', 'FPGA'],
    resortId: 'simonyi',
    logo: '/img/communities/ha5kfu.svg'
  },
  {
    id: 'hk',
    name: 'Hallgatói Képviselet',
    descriptionParagraphs: [
      `Egyik fő feladatunk, hogy összekössük a hallgatókat
    a hivatalos egyetemi szervekkel, érdekeiket képviseljük az egyetemi
    bizottságokban, ahol lehetőségünk van kifejteni az álláspontunkat az oktatásban fennálló
    problémákkal kapcsolatban. Szavazati jogú tagként érdemi beleszólásunk is van ezek megoldásába.
    Minden tanszékkel igyekszünk jó kapcsolatot ápolni, jelezzük feléjük az esetlegesen felmerülő
    problémákat, észrevételeket, valamint az oktatók is bizalommal fordulhatnak hozzánk, –
    példának okáért – egy-egy tantárgyukkal kapcsolatos visszajelzés kapcsán.`,
      `Hallgatótársainknak közvetlenül is alkalmunk nyílik segíteni: gyakran érkezik kérdés
      levelezőlistáinkon tanulmányi, pályázati vagy szociális ügyeket illetően, ha nehéz eligazodni
      a szabályzat-rengetegben. Ennek köszönhetően egy idő után könnyen kiismerjük magunkat a
      szabályzatokban, így az egyedi kérdésekre is tudunk válaszolni.`
    ],
    // imageIds:'https://drive.google.com/open?id=1C-ZYsCrTGpCaKXXyzblSX6LNE9gBMEA8, https://drive.google.com/open?id=1TQCDHdd2FIlGC5eiqLkhINr1F0EtiUFp, https://drive.google.com/open?id=1E_yXtsA9SAYXG8uLMsUr_Q_llS5IynQe, https://drive.google.com/open?id=1kMTml7QWe4TN2apmfe_6S2hRgGPZMO1s, https://drive.google.com/open?id=1FO4TwK1mkEYy3Tpko8ibZySgp8KuyXg9',
    website: 'https://vik.hk/',
    application: 'https://vik.hk/ujonckepzes-jelentkezes/',
    established: '1993',
    email: 'hk@vik.hk',
    members: 30,
    interests: [
      'érdekképviselet',
      'érveléstechnika',
      'fejlődési lehetőség',
      'rendszerismeret',
      'stratégiaalkotás',
      'vezetői technikák',
      'időbeosztás',
      'levélkezelés',
      'emberismeret',
      'projektmenedzselés',
      'kommunikáció',
      'oktatás',
      'pályázat',
      'juttatás',
      'erasmus',
      'szabályzatok'
    ],
    resortId: 'schonherz',
    logo: '/img/communities/hk.svg'
  },
  {
    id: 'hat',
    name: 'Hat',
    descriptionParagraphs: [
      `A HaT (Hallgatói Tudásbázis) a VIK Wiki adminisztrációjáért, illetve
    üzemeltetéséért, fejlesztéséért felelős kör. Nekünk köszönhető a Wiki rendezett állapota.
    Odafigyelünk rá, hogy mindenki kövesse a szerkesztői szokásokat, és rendszeresen felmérjük,
    milyen újításra van kereslet. Azoknak a jelentkezését várjuk, akik nem riadnak vissza egy
    (rosszabb napjain) napi ötezer oldalmegtekintést produkáló rendszer karbantartásától, fejlesztésétől.`
    ],
    resortId: 'kszk',
    logo: '/img/communities/hat.svg'
  },
  {
    id: 'impulzus',
    name: 'Impulzus',
    descriptionParagraphs: [
      `Az Impulzus a Kar és a Kollégium diákújságja.
    A szerkesztőség tagjainak legfőbb feladata, hogy folyamatosan tájékoztassák
    a közönséget nyomtatásban és a lap blogján egyaránt. Azért, hogy ezt minél jobban
    ellássuk, közösségi eseményeket, fesztiválokat, mozikat látogatunk, a komolyabb
    tartalmak felkutatása során pedig gyakran készítünk interjúkat oktatókkal, cégek,
    illetve tanszékek munkatársaival is. A kör tagjai között írók, grafikusok, korrektorok,
    tördelők és webfejlesztők is vannak, mindannyiuknak fontos szerepe van az újság elkészítésében.
    Gólyaimpulzus, Gólyakalauz és Gólyakörte számainkkal igyekszünk minél több segítséget nyújtani a
    gólyáknak, de törekszünk arra is, hogy megismertessük a hallgatókkal a közösségi programokat is:
    erre láthattátok már példának a Qpa utáni számokat és a téli kiadást is. Mindezt 1971 óta, az
    ország egyik legrégebbi és több díjat is nyert diákújságjaként tesszük.`
    ],
    // imageIds: 'https://drive.google.com/file/d/1i5B37vSAZTcuGVkR393szEb2-9di7YrU/view?usp=sharing',
    website: 'http://www.impulzus.com/',
    application: 'https://forms.gle/MkPjaE3roXBh6K739',
    established: '1971',
    email: 'impulzus@impulzus.bme.hu',
    members: 15,
    interests: ['újság', 'grafika', 'cikkírás', 'interjú', 'fotó', 'blog', 'korrektúra', 'tördelés'],
    resortId: 'kultur',
    logo: '/img/communities/impulzus.svg'
  },
  {
    id: 'jatszohaz',
    name: 'Játszóház',
    descriptionParagraphs: [
      `A Játszóház célja, hogy növelje a kollégiumban az öröm/négyzetméter faktort.
      Ezt mindenki előtt nyílt társas-estékkel és ingyenes kölcsönzésekkel biztosítjuk.
      Ha érdekelnek a társasjátékok és keresel jófej, vicces és befogadó közösséget
      (akikkel szinte bármikor társasozhatsz) akkor jelentkezz hozzánk 😃`
    ],
    // imageIds: 'https://drive.google.com/open?id=1EAzLJk4L4nINmfPs9s6qzeNTHq9MWG06',
    // videoIds: 'https://drive.google.com/file/d/1IMp61Q7CwWN9x8JV1YE-BEX8Z14-LG0z/view?usp=drivesdk',
    website: 'https://jatszohaz.sch.bme.hu/news/',
    application: 'https://forms.gle/DJwK9nYicrFeKLtm7',
    established: '2012',
    email: 'tarsas@sch.bme.hu',
    members: 42,
    interests: ['társasjátékok'],
    resortId: 'kultur',
    logo: '/img/communities/jatszohaz.svg'
  },
  {
    id: 'kfb',
    name: 'Kollégiumi Felvételi Bizottság (KFB)',
    descriptionParagraphs: [
      `A kollégiumi felvételivel kapcsolatos összes teendő hozzánk köthető, így a
      munkánk leginkább a szorgalmi időszakok elejére koncentrálódik. `,
      `A leendő kollégisták minden félév elején leadják a felvételi pályázatukat a
      Kollégiumi Egységes Felvételi és Információs Rendszeren (KEFIR-en) keresztül,
      majd epekedve várják az eredményt. A KFB tagjainak feladata az, hogy az egész
      felvételi eljárást és a szobabeosztást levezényeljék. A felvételi kiírás, a
      ponthatárok kialakítása, az átköltözésnél lévő ügyelet vagy a kollégiumi belépőkártyák
      elkészítése mind-mind a mi hatáskörünkbe tartozik.`,
      `Év közben a várólistán lévő hallgatókkal foglalkozunk, gyűléseiken felkészülünk a
      következő felvételi időszakra, továbbá ápoljuk a kollégistákat is érintő hagyományainkat.
      Telente, december 5-én este minden schönherzes ajtóra szaloncukrot ragasztunk, illetve
      minden nőnapon meglepetéssel ébresztjük a kollégium lányait.`,
      `Logónk a piros Schönherz, rajta fekete denevérrel, amely kísértetiesen hasonlít a
      Bacardin is megtalálható denevérre. Harcolunk a Külső Kollégiumi Bizottságban azért,
      hogy minél több villanykaros koliférőhely álljon rendelkezésre a jelentkező kari hallgatóknak.`,
      `Célunk, hogy a kollégiumba jelentkezők minél elégedettebbek legyenek, valamint az, hogy
      a KFB egy jó és hatékony csapatot alkosson.`
    ],
    // videoIds: 'https://drive.google.com/file/d/16miYmYG0xrLDLluH29pZFrrj4AKsqWhz/view?usp=sharing',
    resortId: 'schonherz',
    logo: '/img/communities/kfb.svg'
  },
  {
    id: 'lanosch',
    name: 'Lanosch',
    descriptionParagraphs: [
      `Ha nem rémít meg a videojátékok világa és tudod, hogy csak annak van egy élete aki nem játszik,
      akkor köztünk a helyed!`,
      `A Lanosch nyitásain VIK-es haverokkal, ismerősökkel és leendő barátokkal tudsz eltölteni egy élvezetes
      estét, ahol mindegy, hogy tegnap kezdted a játékot, vagy már évek óta tolod. Itt mindenkit szívesen látunk.`,
      `Ha érdekel az E-sport és a bajnokság szervezés, nálunk egyetemi szintű videojáték bajnokságok lebonyolításába
      nyerhetsz betekintést, akár még országos bajnokságok szervezésébe is bepillanthatsz.`,
      `Ha pedig az éjszakai lan-party életmód érdekel, csatlakozz hozzánk és segíts nyitásainkon. `,
      `LANni, vagy nem LANni, az itt a kérdés!.`,
      `Körvezető: Gál Gyula`,
      `Twitch: https://twitch.tv/lanosch`
    ],
    established: '2012',
    // imageIds:'https://drive.google.com/open?id=1qOsfLkx5xWsk6__G1CQzdzIEyL5q_4yx, https://drive.google.com/open?id=1Hchdl-TZmguPVn7z4-Gj-o2NQ-MvaHg5, https://drive.google.com/open?id=1cQZBHWqdR1s5_z08fu8P29RsDQOIALG2, https://drive.google.com/open?id=1kaXhzKWDGZ2jMtEy1JXZ35scTrDPzZnJ, https://drive.google.com/open?id=1ADV0IVl-WRmpuXMuO2lziOTEAujdmc0-, https://drive.google.com/open?id=19oGFbdLB_pVJ1HLIy71xfJU1VHhCHPwt',
    resortId: 'bulis',
    facebook: 'fb.com/lanoschbme',
    instagram: 'instagram.com/lanosch.gaming',
    logo: '/img/communities/lanosch.svg'
  },
  {
    id: 'laplace',
    name: "La'Place Kávéház",
    descriptionParagraphs: [
      `Ha mindig is érdekelt a sütisütés, hogy hogyan lehet csíkos kávét készíteni, illetve a
    fűszerek világa édességekben, itt a helyed! Hétfői alkalmakkor nyitunk az első emeleten, ahol rengeteg
    finomságot készítünk vendégeinknek. Nagyon csokis forrócsokit, édes turmixokat, különféle muffinokat
    és egyéb édességeket szolgálunk fel a cukorsokkra váró kollégistáknak. Ha érdekelnek az édességek,
    szereted a kávét, megtanulnál különféle sütiket készíteni, ez a kör és lelkes tagjai ölelő karokkal várnak!`
    ],
    established: '2009',
    email: 'kavehaz@sch.bme.hu',
    members: 19,
    interests: ['kávé', 'süti', 'finomságok'],
    resortId: 'kultur',
    logo: '/img/communities/laplace.svg'
  },
  {
    id: 'lego',
    name: 'LEGO Kör',
    color: 'orange',
    descriptionParagraphs: [
      `A LEGO Körben minden olyasmit megtaláltok, ami a robotika tágabban vett területéhez kapcsolódik,
      a robotok építésétől a magas szintű (akár mesterséges intelligenciával támogatott) irányításáig.
      Megismerkedhettek a különböző szenzorok kezelésével (pl.: enkóder, kamera, IMU,…), az adatok feldolgozásával
      beágyazott és asztali környezetben, valamint, hogy ezek alapján hogyan írányíthatjuk a különböző beavatkozó
      szerveket. Mindezt csapatokban dolgozva, baráti társaságban tehetitek, így különböző softskillekkel is fejlődhettek.`,
      `Körünk nevét a dán játékgyártó cég MINDSTORMS készleteiről kapta. Ezeket mára már magunk mögött hagyva tovább
      léptünk a komoly, és az iparban is gyakran használt hardver és szoftver eszközök felé, így összetettebb és
      relevánsabb fejlesztéseket tudunk végezni. Az egyetemi elméleti oktatást kiegészítve lehetőségetek van a
      gyakorlatban is kipróbálni az előadásokon tanultakat, legyetek akár villanyos, akár infós érdeklődésűek.`
    ],
    // imageIds: 'https://drive.google.com/drive/folders/1wehgp5EM5ahmIdtg4E_IEEwmrd4NAfRA?usp=sharing',
    // videoIds: 'https://drive.google.com/file/d/19WuAkBVK6IsILqrOGUynM0GvBTxJhpIL/view?usp=sharing',
    website: 'https://legokor.hu/',
    application: 'https://legokor.hu/tanfolyam/',
    established: '2008',
    email: 'lego@sch.bme.hu',
    members: 36,
    interests: ['robotika', 'MI/AI', 'beágyazott rendszerek', 'alternatív irányítás'],
    resortId: 'simonyi',
    logo: '/img/communities/lego.svg'
  },
  {
    id: 'levelup',
    name: 'LevelUp',
    descriptionParagraphs: [
      `Szeretsz Bulizni? Kipróbálnád magad egy buli szervezőjeként is, nem csak
    résztvevőként? Ha bármelyikre igen a válaszod, akkor itt a helyed a LevelUp csapatban! Hogy mit is
    nyújt a kör? Szervezői oldalról tapasztalhatod meg, hogy milyen, amikor 450-500 ember megfordul
    egy buliban, ez alatt sok rendezvényszervezői tapasztalatot szerezhetsz. Egy kiváló és összetartó
    csapat tagja lehetsz, és nem utolsó sorban életreszóló élményekben lehet részed. Ha felkeltettük az
    érdeklődésed, akkor jelentkezz a LevelUp csapatába!`
    ],
    // imageIds:'https://drive.google.com/open?id=1ujcxORRvzwjcQkMsHSc3cr4BPHSu_oow, https://drive.google.com/open?id=1dUnWBGDzFDRmc0PKAPij6AADmUiqwjlM, https://drive.google.com/open?id=1w3ypu9qW6Iua6Hsw88NgVYzcS52ZTlKa, https://drive.google.com/open?id=1eJh6Uk-w67ej3jfUy22A1lYFsOo4RcNt, https://drive.google.com/open?id=10XncmycHFF428r2-Un1UIWEa_rYGF0QM',
    // videoIds: 'https://drive.google.com/file/d/19hPpM99N35cS5K_nDJGX0mO_Q_GAJ50V/view?usp=sharing',
    website: 'fb.com/SchLevelUp',
    application: 'https://forms.gle/HdrTdvnwPy9e8RMS6',
    established: '2018',
    email: 'levelup@sch.bme.hu',
    resortId: 'bulis',
    logo: '/img/communities/levelup.svg'
  },
  {
    id: 'localheroes',
    name: 'Local Heroes Szerepjátszó Kör',
    descriptionParagraphs: [
      `A Local Heroes tagjai többnyire szerepjátékos  (DnD, Shadowrun, stb.) alkalmakkal foglalkoznak.`,
      `Heti rendszerességgel rendezünk nyitásokat a 120-ban ahol új és régebbi tagjain vagy esetleg újoncaink
      részt vehetnek és kikapcsolódhatnak egy kicsit.`,
      `Amennyiben érdekel a gyűjtögetős kártyajátékok világa (ezen belül is a Magic), akkor nézz le hozzánk
      az egyik alkalomra.`,
      `Nyitásainkról információt a liftközben elhelyezett plakátok biztosítanak.`
    ],
    website: 'https://localheroes.sch.bme.hu/pevents',
    application: 'https://localheroes.sch.bme.hu/how2',
    established: '1994',
    interests: ['szerepjáték', 'DnD', 'Gyűjtügetős Kártyajáték', 'Magic The Gathering'],
    resortId: 'kultur',
    logo: '/img/communities/localheroes.svg'
  },
  {
    id: 'mgmt',
    name: 'MGMT',
    color: 'yellow',
    descriptionParagraphs: [
      `Az MGMT (Menedzsment) kör a Simonyi Károly Szakkollégiumon belül egy csoport, mely
      főként a szakkolis rendezvények megszervezésével és lebonyolításával foglalkozik.
      A tagok a rendezvényszervezés mellett marketing-, PR-, HR- és gazdaságis tapasztalatokra
      is szert tehetnek a körben végzett munkájuk során. Főbb rendezvényeink a Félévnyitó &
      Felvételi Vacsorák, a Simonyi Szakmai Hét, a Félévzáró Szakestek, felvételi táborok,
      a Simonyi Nyári Tábor, illetve a minden év novemberében megrendezésre kerülő Simonyi
      Szülinap, ahová az ország összes szakkollégiumából érkeznek vendégek.`,
      `A szakkollégiumi
      értékek közé tartozik a társadalmi felelősségvállalás is, az ehhez kapcsolódó önkéntes
      programokat is mi szervezzük. A körben kapott menedzsment készségekkel és tapasztalatokkal
      felruházott szakkollégista az itt megszerzett tudást nemcsak az egyetemi pályafutása során,
      hanem annak befejezése után is számos területen tudja kamatoztatni. Legyen szó akár csapatban
      való együttműködésről, prezentálásról, kommunikációs készségekről vagy egy több száz fős esemény
      lebonyolításáról.`
    ],
    //imageIds: 'https://drive.google.com/file/d/1v0OGch-V3ALFpTzHdYnSnKTnOSJXSUw-/view?usp=sharing',
    established: '2018',
    members: 18,
    interests: ['rendezvények', 'társadalmi felelősség'],
    resortId: 'simonyi',
    logo: '/img/communities/mgmt.svg'
  },
  {
    id: 'mmmk',
    name: 'Muzsika Mívelő Mérnökök Klubja',
    descriptionParagraphs: [
      `Az MMMK a zenészek kedvence. Koncertek és élőzenés karaoke estek szervezésén kívül a
      legfőbb feladatunk a kollégiumban található próbatermünkben szervezett zenekari
      és egyéni próbák koordinálása, adminisztrálása, valamint a terem karbantartása.`,
      `Alkalmanként profi előadókat is meghívunk, akik egy est keretében bemutatót tartanak munkásságukról, technikáikról.`,
      `Körvezető: Szurovcsják Ádám`,
      `YouTube: https://www.youtube.com/user/mmmkcsatorna`,
      `Iratkozz fel levlistánkra: https://lists.sch.bme.hu/wws/info/mmmk`
    ],
    // imageIds: 'https://drive.google.com/file/d/1kqkObW4mRES3zJhS2wMd6U1infj4J82K/view?usp=sharing',
    // videoIds: 'https://drive.google.com/file/d/1gTRFUFYqmynZ032db2A14CxqC-RrUY6B/view?usp=sharing',
    website: 'https://mmmk.sch.bme.hu/fooldal/',
    application: 'https://forms.gle/Xy5sNWNWuaagPtZp9',
    facebook: 'https://www.facebook.com/muzsikamivelomernokokklubja',
    instagram: 'https://www.instagram.com/mmmk.official/',
    established: '2002',
    email: 'mmmk@sch.bme.hu',
    members: 28,
    interests: ['zene'],
    resortId: 'kultur',
    logo: '/img/communities/mmmk.svg'
  },
  {
    id: 'neteam',
    name: 'Neteam',
    descriptionParagraphs: [
      `A NETeam foglalkozik a kollégiumi hálózati infrastruktúra üzemeltetésével és fejlesztésével.
      Biztosítjuk a hálózat folyamatos működését, követjük az új hálózati technológiák alakulását,
      igyekszünk gyakorlatban is kipróbálni őket, alkalomadtán pedig cégekkel kapcsolatot tartva veszünk
      részt új megoldások tesztelésében is.`,
      `A Schönherzben a ‘90-es évek óta üzemelteti a Házat lefedő hálózatot a KSZK, ami azóta többször
      teljes cserén esett át. A KSZK reszorttá válása előtt a NETeam egy erre szakosodott csoportja volt,
      2014 óta pedig önálló körként folytatjuk tevékenységünket.`,
      `A körbe csatlakozóknak lehetőségük van bekapcsolódni az ISO/OSI modell majdnem minden rétegében
      működő szolgáltatások üzemeltetésébe és fejlesztésébe. Ilyen például a Cisco eszközök konfigurációja,
      NAT megoldások, VLAN-ok, RADIUS, WiFi, IDS, DNS/Dinamikus DNS szolgáltatások üzemeltetése, fejlesztése,
      felhasználói adatbázis kezelése, forgalommonitorozás és konfiguráció menedzsment. Ezek széleskörű rálátást
      engednek egy komplex hálózatra, amelynek több mint 1000 végpontja van. A használt hálózattól függetlenül
      kialakított router laborban pedig lehetőség van szinte bármilyen, az iparban használt switching/routing
      megoldás kipróbálására.`,
      `Több párhuzamosan futó projektünk is van, például NAT detektálása, NAT gateway kialakítása, DNS üzemeltetése,
      új kollégiumi WiFi hálózat kiépítése, de mostanában ismerkedünk Ansible alapú konfiguráció menedzsmenttel és
      NetBox-szal is, illetve szívesen várunk saját ötleteket!`,
      `Aki bekerül a csapatba és hálózatot vagy szolgáltatásokat üzemeltet, fejleszt, felelősséggel fog tartozni az
      általa irányított rendszerért, ezzel pedig fontos része lesz a schönherzes közéletnek. Ez a felelősség, az
      ezzel járó nem kizárólag szakmai tapasztalat és technikai tudás pedig egy későbbi önéletrajzban is olyan
      tényező tud lenni, amivel kevés frissen végzett hálózati szakember rendelkezik.`
    ],
    resortId: 'kszk',
    logo: '/img/communities/neteam.svg'
  },
  {
    id: 'palacsintazo',
    name: 'Palacsintázó',
    descriptionParagraphs: [
      `2008 óta sütünk a palacsintákat a kolisoknak, mindig valamelyik tanulóban várjuk a
      kedves vendégeket. A választékban szerepel például hortobágyi húsos, bolognai szószos,
      nutellás-banános, gesztenyés-tejszínes, pudingos, különféle gyümölcsösök és még sok más
      fajta is. A tagok közös jellemzője: imádják a palacsintákat – enni és sütni egyaránt.
      Heti rendszerességgel tartunk összejöveteleket, ahol iszogatva, beszélgetve kiengedjük a
      fáradt gőzt egy kellemes, barátságos társasággal.`
    ],
    resortId: 'szor',
    logo: '/img/communities/palacsintazo.svg'
  },
  {
    id: 'pizzasch',
    name: 'Pizzásch',
    descriptionParagraphs: [
      `Pizza, Innováció, Nők, Aranyélet.`,
      `Szereted a pizzát? Szeretnél kipróbálni valami újat? Szeretnéd kipróbálni magad egy 10/10-es csapat tagjaként?`,
      `Akkor itt a helyed.`,
      `A Pizzáschban minden szerda este előre leadott rendelések alapján pizzával látjuk el az éhező kollégisákat.`,
      `Kínálatunkban a már ismert pizzafajtáktól kezdve bármilyen új ötletedig megvalósíthatod álmaid pizzáját.`,
      `A szerda esték mellett persze gólyatáborban, gólyabálon, bulikon és külsős rendezvényeken is találkozhattál már velünk.`,
      `Ha szeretnél a csapat tagja lenni, itt a lehetőség!`,
      `Alapítás éve: 2004`,
      `Körvezető: Melik Gábor (Apuci)`
    ],
    // imageIds:'https://drive.google.com/open?id=1DptHvN48aZ3CKB7xhWgYYWD8dTZdb272, https://drive.google.com/open?id=1K5BosVlqUYYJFqD2ES_56inhqG3cRqT_, https://drive.google.com/open?id=1wzDXFn8134cOIZ_G9hxc0K4MK976_LKa, https://drive.google.com/open?id=1gNWxuizsX5y5XReS2gFF0H5X66lOwSUE, https://drive.google.com/open?id=1catZMbo5rh_YI3nESqYs6HU3a0v5kOT2, https://drive.google.com/open?id=19ttK9ed1EtjVmSyqlgLR0FKu27zkj0Y2, https://drive.google.com/open?id=1zF9rrhYZvWGQDjD_YXyUPrTsz-E_OcWj, https://drive.google.com/open?id=1KBHKImAoDPu4vdlOGtCAF-Rwz7JXApGP, https://drive.google.com/open?id=12LdnE-WpFDIJzLq1Tt3JbyfO9TfCMqx5, https://drive.google.com/open?id=1a2XJGqyvp0UhfCWaodvfYjwRjAuEUkVW',
    // videoIds: 'https://drive.google.com/file/d/1k2cNoONyho8Zg_AErnhLiwLVrdMoIeeW/view?usp=sharing',
    website: 'http://pizzasch.sch.bme.hu/',
    established: '2004',
    interests: ['pizzát sütünk'],
    resortId: 'szor',
    logo: '/img/communities/pizzasch.svg'
  },
  {
    id: 'reggelisch',
    name: 'reggelisch',
    descriptionParagraphs: [
      `Lelkes tagjaink már 2018 ősze óta várják minden héten a reggelizni vágyó
      éhes egyetemistákat. Kínálatunkban megtalálható a rántotta, a bacon, a
      bundáskenyér és a pancake is, amit a sok szeretet és egy pohár forró
      tea vagy narancslé tesz még finomabbá. A háttérben szóló kellemes
      zene barátságos légkört teremt a helyszínen fogyasztóknak, de reggelidet
      akár a szobádba is kérheted előrendeléssel.`
    ],
    //    videoIds: 'https://drive.google.com/file/d/1OcCSximEoWNogZ56n8Gk23V1QApT-drR/view?usp=sharing',
    established: '2019',
    email: 'reggelisch@gmail.com',
    interests: ['reggeli készítés'],
    resortId: 'szor',
    logo: '/img/communities/reggelisch.svg'
  },
  {
    id: 'schdesign',
    name: 'schdesign',
    color: 'red',
    descriptionParagraphs: [
      `Az schdesign a Simonyi Károly Szakkollégium kreatív alkotóműhelye.
    Tagjainknak lehetőségük van kipróbálni magukat a különböző digitális esztétikai műfajokban,
    mint például arculattervezés, logókészítés, webdesign, UX design, 3D modellezés vagy egyéb
    nyomtatott grafikák készítése. Mindezt valós és izgalmas projekteken keresztül: egyik legnagyobb
    felkérésünk az évről-évre megrendezésre kerülő Simonyi Konferencia arculatának megtervezése.
    Leggyakrabban használt programjaink az Illustrator, a Photoshop, a Figma és a Blender. Ezeken
    felül még számtalan más eszközt is igénybe szoktunk venni, a lehetőségek nincsenek korlátozva.
    Digitális rajztábláink szabadon hozzáférhetőek tagjainknak klubszobánkban és az iPadünket is
    bármikor elkérhetik a tagok. Az érdeklődőknek minden tavaszi félévben tartunk workshopokat,
    ahol az alapoktól mutatjuk meg Nektek a webfejlesztés, a vektor- és rasztergrafika, illetve
    a 3D modellezés eszközeit.`
    ],
    // imageIds: 'https://drive.google.com/open?id=1H879ISYLczwYlqNfvdIW-LW94_E9od3d',
    website: 'https://schdesign.hu',
    established: '2010',
    email: 'hello@schdesign.hu',
    members: 42,
    interests: [
      'plakátok',
      'matricák',
      'látványtervek',
      'frontend',
      'webfejlesztés',
      '3d',
      'modellezés',
      'blender',
      'photoshop',
      'illustrator',
      'figma',
      'vscode'
    ],
    resortId: 'simonyi',
    logo: '/img/communities/schdesign.svg'
  },
  {
    id: 'sem',
    name: 'Schönherz Elektronikai Műhely',
    color: 'green',
    descriptionParagraphs: [
      `A Schönherz Elektronikai Műhely a Rádiótechnikai Diákkör újjászületése,
      ami Simonyi Károly Szakkollégium egyik legrégebbi köre. Fő célja az
      elektronika gyakorlati oldalának bemutatása az egyetemista diákok
      számára, amellyel az érdeklődési körüknek megfelelő területen tovább
      mélyíthetik ismereteiket, a műhely eszközeit felhasználva. Így a személyes
      motiváció által az elméleti és gyakorlati ismereteiket fejlesztve, a tanulás
      leghatékonyabb formáját sajátíthatják el. Végül önerőből magasabban kvalifikált mérnökké válhatnak.`
    ],
    // imageIds: 'https://drive.google.com/open?id=1oLCuRERGpl-Y59CENIWuTofTT8Aw5aJj',
    resortId: 'simonyi',
    logo: '/img/communities/sem.svg'
  },
  {
    id: 'svk',
    name: 'Schönherz Vállalati Kapcsolatok',
    descriptionParagraphs: [
      `Célunk, hogy közelebb hozzuk hozzátok szakmai partnereinket, ami
      által megismerhetitek az aktuális piaci szereplőket. Nálunk elsősorban
      a kommunikáció és sales képességek fejlesztésén van a hangsúly. A
      szükséges tanfolyamok után lehetőségetek van élesben kipróbálni magatokat
      céges tárgyalások keretein belül. Megismerhetitek milyen szakmai eseményeket
      szervezni, illetve lehetőségetek van levezényelni egy MeetUpot.`
    ],
    // imageIds: 'https://drive.google.com/drive/folders/1Y6ZgtqqXEgCLkwL2Ip1YB_N9iIGEUjCn?usp=sharing',
    // videoIds: 'https://drive.google.com/file/d/135UMGzoKn9Ke1cb6kfjpuSVPVkukYOkh/view?usp=sharing',
    application: 'https://docs.google.com/forms/d/1Pj7NvO2JJWieO80w8_vLQgVIz24cOiWZWaIeyqLics0/edit',
    established: '2003',
    email: 'svk@sch.bme.hu',
    members: 6,
    interests: ['employer branding', 'céges kommunikáció', 'tárgyalás', 'önfejlesztés'],
    resortId: 'schonherz',
    logo: '/img/communities/svk.svg'
  },
  {
    id: 'schorpong',
    name: 'Schörpong',
    descriptionParagraphs: [
      `A Schörpong 2017 őszén indult útjára, 8 lelkes, sörpongozni vágyó
      fiatal által. Az első őszi nyitás hatalmas siker lett, a bajnokság
      első fordulójában labdák pattogtak szerte az FNT-ben. Innentől nem
      volt megállás…`,
      `A Schörpong csapata alapvetően egy egész éven átívelő bajnokságot szervez
      a kollégistáknak, mely során az adott forduló győzteseit is jutalmazzuk.
      A fordulókban a legjobb csapatok pontokat szerezhetnek, ezeket év végén
      összesítjük és így kerül ki a Schörpong bajnok. Mindezen túl, a félév
      alatt rendelkezésre állunk, ha valakinek szettre lenne szüksége!`
    ],
    resortId: 'bulis',
    logo: '/img/communities/schorpong.svg'
  },
  {
    id: 'securiteam',
    name: 'Securiteam',
    descriptionParagraphs: [
      `A SecurITeam-ben te is megismerkedhetsz az IT biztonságban használt technikákkal: például weboldalak,
      szerverek és fizikai zárak feltörésével foglalkozik, de a hardver sem áll tőlünk távol. Csináltál egy
      weboldalt vagy appot, de nem vagy meggyőződve arról, hogy biztonságos? Nálunk mind a támadó, mind a
      védekező oldal technikáiról tanulhatsz, és ki is próbálhatod a tudásodat élőben. Az SQL injection,
      programok visszafejtése és exploitálása, malware-ek elemzése, webes alkalmazások biztonságának vizsgálata,
      gyenge titkosítások megkerülése, új megoldások (pl. microservicek) biztonságának megismerése, valamint
      a privacy védelme mind olyan dolgok, amivel körünk foglalkozik. A szoftveres témák mellett a fizikai
      biztonságra is hangsúlyt fektetünk, legyen az Bluetooth sniffing, rádiós lehallgatás, RFID klónozás
      vagy lockpicking, náluk megtalálod az ehhez szükséges eszközöket és szaktudást!`,
      `Előadásokat, tanfolyamokat és workshopokat tartunk, illetve a schönherzes infrastruktúra biztonságát
      rendszeresen pentestekkel ellenőrizzük.`,
      `Nálunk lehetőséged adódik arra, hogy a korábban felsorolt tevékenységek bármelyikével foglalkozz, tanulj,
      de akár IT biztonsági versenyeken (CTF) is indulhatsz. Ezen felül van saját fejlesztésű CTF-ünk is, amit
      minden ősszel rendezünk meg, a Wargame.`
    ],
    resortId: 'kszk',
    logo: '/img/communities/securiteam.svg'
  },
  {
    id: 'spot',
    name: 'SPOT',
    descriptionParagraphs: [
      `A SPOT 1961-es alakulásával a Schönherz második legrégebbi, ma is működő köre. Tevékenységünk
      két fő részből áll: rendezvényfotózásból és stúdiófotózásból, utóbbinak a 1419-es szobában
      található műtermünk ad helyet. Felszerelésünket Nikon és Canon gépvázak, rendszervakuk és
      objektívek alkotják, amik között a halszemobjektívtől a nagy fényerejű portréobjektíven át
      a teleobjektívig számos különböző darab megtalálható.`,
      `A kar és a kollégium majdnem minden rendezvényét megörökítjük, és publikáljuk az albumokat
      a weboldalunkra. Néhány rendezvényen más feladatokat is ellátunk, például a Gólyatáborban minden
      nap végén egy videót készítünk az adott nap fotóiból, illetve a Gólyabálon egy fotósarkot alakítunk
      ki, ahol kérésre lefotózzuk a résztvevőket, majd helyben retusáljuk és ki is nyomtatjuk a képeket.
      A rendezvények fotózása során a körtagoknak lehetőségük nyílik elmélyíteni tudásukat konferencia-,
      tánc-, koncert-, sport- és bulifotózás területén is.`,
      `Műtermünkben a profi stúdiófelszerelés segítségével sokszor készítünk a kar hallgatói számára
      igazolványhoz és önéletrajzhoz fotókat, de szívesen vállalunk vagy szervezünk saját magunknak
      bármilyen egyéb fotózást, legyen az akár portré, akár tárgyfotózás.`,
      `A fényképezéssel kapcsolatos technikai tudást a körtagok egymástól tanulják meg.
      Ezt a célt szolgálja a tavaszi tanfolyam, ahol szó esik a digitális tükörreflexes
      gépek működéséről, beállításairól, fotók utómunkázásáról a legnépszerűbb szoftverek
      (Adobe Photoshop és Adobe Photoshop Lightroom) segítségével, a rendezvényfotózás elvi
      kérdéseiről és a műtermi fotózás alapjairól is. A tanulás elősegítése érdekében az őszi
      félévben is tartunk tanfolyamot haladóbb témákban, szervezünk fotósétákat, illetve a taggá
      válásig a tapasztaltabb körtagjaink mentorálják az újoncokat, ezzel segítve a szakmai
      fejlődésüket. A fotózás mellett a weblapunkat és az azt kiszolgáló szervereket is mi
      fejlesztjük és üzemeltetjük.`
    ],
    // imageIds: 'https://drive.google.com/file/d/1kOXp_1be55THLKGYS_niLvB2rORqyFI2/view?usp=sharing',
    // videoIds: 'https://drive.google.com/file/d/1uqLa7mptazsi0jFennpKOOSp8K-Ag_8n/view?usp=sharing',
    website: 'https://spot.sch.bme.hu',
    application: 'https://spot.sch.bme.hu/tanfolyam',
    established: '1961',
    email: 'spot@simonyi.bme.hu',
    members: 30,
    interests: ['Rendezvényfotózás', 'Műtermi fotózás', 'Lightroom', 'Photoshop'],
    resortId: 'simonyi',
    logo: '/img/communities/spot.svg'
  },
  {
    id: 'sysadmin',
    name: 'Sysadmin',
    descriptionParagraphs: [
      `A Sysadmin a Schönherz szerverüzemeltetésével foglalkozó öntevékeny köre.
      A kollégiumban működő IT szolgáltatásokat nyújtó infrastruktúrát felügyeljük,
      fejlesztjük. A Ház weboldalainak túlnyomó része a mi általunk kezelt szervereinken
      fut, feladatunk, hogy ezek minél kevesebb leállással, gyorsan és biztonságosan üzemeljenek.
      Az általunk használt szoftverek között megtalálhatóak Linux, valamint Windows alapú megoldások
      is. A RemoteApp szolgáltatásnak köszönhetően számos népszerű és az egyetemi tanulmányok során
      fontos alkalmazást telepítés nélkül használhat minden VIKes. Legfiatalabb a Kubernetes szolgáltatásunk,
      amely az iparban is újdonságnak és népszerűnek számító konténerizációs technológián alapul.`,
      `Minden félévben indítunk új projekteket. Az elmúlt időszakban komoly hangsúlyt fektettünk a
      monitoring rendszerünk fejlesztésére, mely jelenleg is aktív fejlesztés alatt áll.`,
      `Fő profilunk az automatizálás lett, mely segítségével sokkal könnyebben tudjuk fenntartani,
      valamint fejleszteni a szolgáltatásainkat, és a kísérletezés lehetőségét is meghagyja, hiszen
      egy kattintással vissza tudjuk állítani a korábbi verziókat. Népszerű technológiák, amikkel
      automatizálás terén foglalkozunk: Ansible, Terraform, CI/CD. `,
      `Néhány rendszerünk, amik mostmár teljesen vagy közel teljesen automatizáltak: az új Kubernetes
      klaszterünk, VMware nagy része, storage szerverünk, és még jó sok más.`,
      `Ha a fizikai adattárolás rejtelmei érdekelnek, akkor szintén itt a helyed, hiszen megtalálod
      nálunk a Ház legnagyobb storage klaszterét.`,
      `Az új dolgok mellett persze komoly hangsúlyt fektetünk a jelenleg üzemelő szerverek folyamatos
      karbantartására is. Nálunk megtanulhatsz Linux-ul, valamint, hogy hogyan kell egy több fizikai
      hosztból álló, 80 EPYC magot és 640GB RAMot tartalmazó, körülbelül 100 virtuális szervert kiszolgáló
      virtualizációs klasztert üzemeltetni, de akár a webhosting területén is fejlesztheted tudásod a Ház
      legnagyobb (~100 weboldalt kiszolgáló) szerverének felügyelete során.`,
      `De ne aggódj, nem csak Linuxos rendszereink vannak, ha a Windows érdekel, akkor is van bőven lehetőség
      számodra, hiszen a Ház szintű levelezés, a RemoteApp és az authentikációhoz oly fontos Active Directory
      szolgáltatásunk is a Windows-os klaszterünkben található meg.`,
      `Ha szeretnél a felszín mögé látni, menő szerverekkel és rendszerekkel kísérletezni, új technológiákat
      megismerni vagy megismertetni másokkal és releváns szakmai tapasztalatot szerezni a területen, akkor
      köztünk a helyed. Ha úgy érzed, hogy el vagy veszve a témákban, de érdekel a szerverüzemeltetés világa,
      ne csüggedj: az előismeret nem követelmény, a lelkesedés igen.`
    ],
    resortId: 'kszk',
    logo: '/img/communities/sysadmin.svg'
  },
  {
    id: 'szauna',
    name: 'Szauna Kör',
    descriptionParagraphs: [
      `A kör neve magáért beszél, hiszen nekünk köszönhetően abban a
    szerencsés helyzetben van a kollégiumunk, hogy nem kell messzire mennünk egy pihentető
    szaunázásért. A 219-es klubszobában üzemben lévő 8-fős finn szauna hetente több alkalommal
    is szeretettel várja az egészséges kikapcsolódásra vágyókat. Nyitásokkor minden hatás a
    megfáradt kollégák pihentetését szolgálja, többek között a lágy zene, kellemes társalgás,
    illatos forró gőzök, valamint az ezt követő friss levegő és hűs pohár víz, mindez kollégista
    pénztárcához igazítva. Ezek igénybevételéhez a honlapunkon lehet időpontot foglalni a meghirdetett alkalmaknál.`,
      `Csak úgy, mint a vendégeket, az új tagokat is várjuk sok
    szeretettel. Amennyiben te is szeretsz szaunázni, vagy érdekel a kultúra, bármelyik körtag
    felkeresésével jelentkezhetsz a körbe.`
    ],
    website: 'https://szauna.sch.bme.hu/',
    established: '2012',
    email: 'sch-szauna@googlegroups.com',
    members: 14,
    interests: ['szauna'],
    resortId: 'szor',
    logo: '/img/communities/szauna.svg'
  },
  {
    id: 'sssl',
    name: 'Szent Schönherz Senior Lovagrend',
    descriptionParagraphs: [
      `A Szent Schönherz Senior Lovagrend feladata a friss egyetemisták, elsőévesek segítése és
      támogatása. Minden évben rengeteg közösségi programot és tanulmányi segítséget igyekszik
      nyújtani az érdeklődőknek. Annak érdekében, hogy mindig legyen, aki ezt a hagyományt tovább
      viszi, a Lovagrend képzést indít, ahol tudását átadja az arra kíváncsi újoncoknak. `,
      `A képzés nagyjából két hónapot ölel fel, hetente egy estédet veszi igénybe. Ez alatt az idő alatt,
      megismerheted és megtanulhatod, mivel és hogyan foglalkozik egy senior.`
    ],
    // videoIds: 'https://drive.google.com/file/d/1QBJQFFHEK3FUA_5LV3dLL0caoH-pa1zb/view?usp=sharing',
    website: 'https://sssl.sch.bme.hu/',
    application: 'kepzes.sch.bme.hu',
    established: '1992',
    email: 'sssl@sch.bme.hu',
    members: 500,
    interests: ['gólyák'],
    resortId: 'schonherz',
    logo: '/img/communities/sssl.svg'
  },
  {
    id: 'vodor',
    name: 'Vödörkör',
    descriptionParagraphs: [
      `Vödör FAQ`,
      `P: Mit?`,
      `E: Krumplit`,
      `P: Mikor?`,
      `E: Hétfőnként`,
      `P: Hol?`,
      `E: Nagykonyha`,
      `P: Kiknek?`,
      `E: Éheseknek`
    ],
    // videoIds: 'https://drive.google.com/file/d/14UUn6XQy6PJpDVr6Qgzn015zdO8lJl5e/view?usp=sharing',
    website: 'https://vodor.sch.bme.hu/',
    established: '2002',
    email: 'vodor@sch.bme.hu',
    members: 17,
    interests: ['krumpli'],
    resortId: 'szor',
    logo: '/img/communities/vodor.svg'
  },
  {
    id: 'wtf',
    name: 'WTF',
    descriptionParagraphs: [
      `Nálunk ki ereszthetitek a fáradt gőzt és megtölthetitek pangó tüdőtőket vizipipa füsttel.`,
      `Ha bármi kérdésetek és kérésetek van vizpipázással vagy eszközkölcssönzéssel kapcsolatban, a
      választ nálunk garantáltan megtaláljátok!`
    ],
    // videoIds: 'https://drive.google.com/file/d/1a1lLve63YgGUJJDzaau3MFghdUGfDOlJ/view?usp=sharing',
    established: '2006',
    email: 'wtf@sch.bme.hu',
    members: 12,
    interests: ['pipa'],
    resortId: 'szor',
    logo: '/img/communities/wtf.svg'
  },
  {
    id: 'sat',
    name: 'Schönherz Airsoft Team',
    descriptionParagraphs: [
      `Az airsoft olyan, mint az építészet: pár milliméteren múlhatnak emberéletek.
      Na nem valódi életek, hiszen ez „csak” egy játék. Játék nagyra nőtt gyerekeknek
      nagy és nehéz játékszerekkel. Ez az a rész, ahol a Schönherz Airsoft Team jön a
      képbe, ugyanis mi nagyon szeretünk ezzel a sporttal, valamint annak játékszereivel
      foglalkozni – legyen az játékok szervezése, a fegyverek szerelése vagy csak egy kis
      esti lövöldözés, mindezt egy nagyszerű közösségben! A nálunk szerzett élmény megfizethetetlen,
      minden másra pedig ott a MasterCard!`
    ],
    //imageIds: 'https://drive.google.com/open?id=1UkEXOLq9jy_DQUTuZpDhRXS0Sv5Q9G3X',
    //videoIds: 'https://drive.google.com/file/d/1os-Bx0aP9nmFZwuU-gopzHX4r1bdnAXR/view?usp=sharing',
    website: 'https://airsoft.sch.bme.hu/',
    application: 'https://forms.gle/xFnEEra9CsVgzJBs6',
    established: '2020',
    email: 'airsoft@sch.bme.hu',
    members: 12,
    interests: ['airsoft'],
    resortId: 'sport',
    logo: '/img/communities/sat.svg'
  },
  {
    id: 'szakestely',
    name: 'VIK Szakestély',
    descriptionParagraphs: [
      `A kör a selmeci diákhagyományok és a Miskolci Egyetem Szakestélyei alapján
      (azokhoz csak hasonló, de velük nem megegyező) Szakestélyeket és Szakesteket
      szervez. Ezek célja, hogy félévente két alkalommal összeüljenek a baráti társaságok,
      és egy meghatározott program szerint különböző vicces előadásokat hallgathassunk
      meg tanárainktól, mindeközben sört/fröccsöt és zsíroskenyeret fogyasszunk, illetve
      a Szakestes nótákat énekeljük. Az általában októberben megrendezett Korsóavató
      Szakestély célja, hogy a gólyák előadása alapján kiderüljön, hogy érdemes-e az
      évfolyam arra, hogy felavassák Szakestes korsóikat.`,
      `Másik nagyobb volumenű Szakestélyünk
      a Gyűrűavató Szakestély volt, ezen a végzett hallgatók avathatták fel a Villanykaros
      Gyűrűjüket. A Gyűrűavató Szakestélyek újra-bevezetése jelenleg is folyamatban van.
      Ezen komolyabb hangvételű Szakestélyek mellett szoktunk kisebb, lazább Szakesteket
      is tartani, a félév elején Gatyábarázót, a szorgalmi időszak végén pedig Búfelejtő
      Szakesteket. Ezeknek a célja pusztán a szórakoztatás.`
    ],
    website: 'szakest.sch.bme.hu',
    established: '2001',
    email: 'vik-szakest@sch.bme.hu',
    members: 50,
    interests: ['szakestek'],
    resortId: 'bulis',
    logo: '/img/communities/szakestely.svg'
  },
  {
    id: 'kbpr',
    name: 'KBPR',
    descriptionParagraphs: [
      `A KB-PR a kollégiumi információterjesztés alappillére. Ipari teljesítményű nyomtatónkkal
      magas minőségben adjuk ki a körök által igényelt plakátokat, de ezen kívül is rengeteg
      nyomdai szolgáltatást nyújtunk a laminálástól egészen a matricák készítéséig. A Ház
      kulturális fejlődésében is nagy szerepet játszunk, hiszen mi készítjük el a hetente
      megjelenő KultúrWC-t, de a forgóvilláknál található plakát tervezése és kihelyezése
      is a mi feladatunk. Felügyeljük a program.sch.bme.hu weboldalt is, ahol a Házban
      történő szinte összes eseményről részletes információt szerezhetsz. S habár mindenki
      szereti a színes liftajtókat, a lejárt események plakátjai sem maradhatnak fent
      örökké: ezek eltávolítása is a mi feladatunk.`
    ],
    website: 'https://kbpr.sch.bme.hu',
    application: 'https://forms.gle/66ofR5EQ19sB8BCf6',
    established: '2003',
    email: 'kbpr@sch.bme.hu',
    members: 20,
    interests: ['nyomtatás'],
    resortId: 'schonherz',
    logo: '/img/communities/kbpr.svg'
  }
]
