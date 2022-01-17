import { Organization } from '../types/Organization'

export const RESORTS: Organization[] = [
  {
    id: 'simonyi',
    name: 'Simonyi Károly Szakkollégium',
    shortDescription: 'A VIK Szakkollégiuma.',
    description: `A Simonyi Károly Szakkollégium célja,
    hogy elsősorban a szakkollégium tagjai, lehetőség
    szerint a Villamosmérnöki és Informatikai Kar minden
    hallgatója számára lehetőséget biztosítson az egyetemi képzést kiegészítő ismeretek elsajátítására.`,
    website: 'https://simonyi.bme.hu',
    logo: 'https://logotar.schdesign.hu/preview/Simonyi_preview.png',
    color: 'green',
    established: '2003.',
    email: 'info@simonyi.bme.hu',
    members: 180
  },
  {
    id: 'szor',
    name: 'Szolgáltató Reszort (SZOR)',
    shortDescription: 'Nem kulturális jellegű szolgáltatást nyújtanak a Schönherz Zoltán Kollégium lakóinak.',
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
    id: 'egyeb',
    name: 'Egyéb körök',
    description: `Ezek a körök nem tartoznak reszort alá.`
  }
]
