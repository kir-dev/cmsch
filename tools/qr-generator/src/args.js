import yargs from 'yargs/yargs'
import fs from 'fs'
import { hideBin } from 'yargs/helpers'
import { fileToBase64 } from './util.js'

export default yargs(hideBin(process.argv))
  .version('4.20')
  .option('count', {
    type: 'number',
    description: 'Hány QR kód készüljön',
    demandOption: true
  })
  .option('tokenLength', {
    type: 'number',
    description: 'Hány karakteres legyen a token',
    default: 69
  })
  .option('tokenPrefix', {
    type: 'string',
    description: 'A generált tokenek prefixe (beleszámolódik a hosszba)',
    default: 'KIR-CODE_'
  })
  .option('qrOutDirectory', {
    type: 'string',
    description: 'Ide kerülnek lementésre a QR kódok'
  })
  .option('csvOutputPath', {
    type: 'string',
    description: 'CSV kimenet helye',
    default: 'out.csv',
    demandOption: true
  })
  .option('name', {
    type: 'string',
    description: 'A token elnevezése {index} helyére a sorszám kerül',
    default: ''
  })
  .option('visible', {
    type: 'boolean',
    description: 'Olvasható token kerüljön-e generálásra',
    default: true
  })
  .option('type', {
    type: 'string',
    description: 'Token típusa',
    default: ''
  })
  .option('icon', {
    type: 'string',
    description: 'Leolvasáskor megjelenő kép urlje',
    default: ''
  })
  .option('score', {
    type: 'number',
    description: 'Leolvasásért járó pontszám',
    demandOption: true
  })
  .option('rarity', {
    type: 'string',
    description: 'Token rarityje',
    choices: ['COMMON', 'UNCOMMON', 'RARE', 'EPIC', 'LEGENDARY', 'RAINBOW', ''],
    default: ''
  })
  .option('action', {
    type: 'string',
    description: 'QR fighthoz az akció amit kivált. capture:<tower>, history:<tower> vagy enslave:<tower>',
    default: ''
  })
  .option('activeTarget', {
    type: 'string',
    description: 'Aktív cél | Csak akkor ha a QR Fight komponens is be van töltve',
    default: ''
  })
  .option('displayIconUrl', {
    type: 'string',
    description: 'Kijelzett kép URL-je',
    default: ''
  })
  .option('displayDescription', {
    type: 'string',
    description: 'Kijelzett szöveg',
    default: ''
  })
  .option('availableFrom', {
    type: 'number',
    description: 'Scannelhető innentől'
  })
  .option('availableUntil', {
    type: 'number',
    description: 'Scannelhető eddig'
  })
  .option('frontendUrl', {
    type: 'string',
    description: 'A CMSch frontend urlje',
    demandOption: true
  })
  .option('qrCodeStyling', {
    type: 'string',
    description: 'Elérési útvonal a https://qr-code-styling.com/ oldalon generált JSON filehoz',
    demandOption: false
  })
  .option('generateQrCodes', {
    type: 'boolean',
    description: 'Generálásra kerüljenek-e a QR kódok',
    default: false
  })
  .option('qrRendererTemplate', {
    type: 'string',
    description: 'A QRek generálásához használt template',
    default: 'assets/render-template.html'
  })
  .option('qrImage', {
    type: 'string',
    description: 'QR Kódba kerülő kép'
  })
  .check((argv) => {
    if (argv.generateQrCodes && !argv.qrCodeStyling) {
      throw new Error('A QR Kódok generálásához szükséges a beállítás JSON megadása!')
    }
    return true
  })
  .check((argv) => {
    if (argv.generateQrCodes) {
      if (!argv.qrOutDirectory) {
        throw new Error('A QR kódok generálásához szükséges a mentés helyének megadása')
      } else if (fs.readdirSync(argv.qrOutDirectory).length > 0) {
        throw new Error('A QR kódok generálásához megadott könyvtár nem üres')
      }
    }
    return true
  })
  .coerce('qrOutDirectory', (arg) => {
    fs.mkdirSync(arg, { recursive: true })
    return arg
  })
  .coerce('qrCodeStyling', (arg) => {
    if (!fs.existsSync(arg)) {
      throw new Error('A beállítás JSON file nem létezik')
    }
    const content = fs.readFileSync(arg, 'utf8')
    return JSON.parse(content)
  })
  .coerce('qrImage', (arg) => {
    if (!arg) return undefined
    if (!fs.existsSync(arg)) {
      throw new Error('A qr kódhoz tartozó kép nem létezik')
    }
    return fileToBase64(arg)
  })
  .coerce('frontendUrl', (arg) => {
    const url = new URL(arg)
    return url.origin
  })
  .parseSync()
