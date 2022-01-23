import { Community } from '../types/Organization'

export const COMMUNITIES: Community[] = [
  {
    id: 'kirdev',
    name: 'Kir-Dev',
    shortDescription: 'A kollégium webfejlesztő köre.',
    description: `A Kollégiumi Információs Rendszer Fejlesztői és Üzemeltetői
      – röviden Kir-Dev – a BME VIK hallgatóiból álló webfejlesztő csapat.
      Körünk 2001-ben alakult, 2009 óta a Simonyi Károly Szakkollégium része.
      Alakulásunk óta foglalkozunk különféle webes technológiák alkalmazásával és oktatásával,
      valamint fejlesztünk és üzemeltetünk a kollégiumi közösség számára hasznos webes alkalmazásokat.`,
    interests: ['PéK', 'TanulóSch', 'SchPincér', 'InduláSch', 'egyéb webes megoldások'],
    website: 'https://kir-dev.sch.bme.hu',
    established: '2001.',
    email: 'kir-dev@sch.bme.hu',
    members: 17,
    resortId: 'simonyi',
    logo: '/img/communities/kirdev.svg',
    color: 'orange',
    images: ['https://kir-dev.sch.bme.hu/static/694736fc08b01fcbab76646a0b403c64/678ad/pek-next.webp']
  },
  {
    id: 'schdesign',
    name: 'schdesign',
    shortDescription: 'Az schdesign a Simonyi Károly Szakkollégium kreatív alkotóműhelye.',
    description: `Körünk UI, UX, illetve digitális designnal foglalkozik.
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
