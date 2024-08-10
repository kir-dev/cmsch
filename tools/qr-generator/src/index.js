import fs from 'fs'
import args from './args.js'
import { stringify } from 'csv-stringify/sync'
import { generateTokens } from './generator/token.js'
import { generateQrCodes } from './generator/qr.js'

const tokens = generateTokens(args)
const csvOptions = { header: true, cast: { boolean: value => value ? 'true' : 'false' } }
fs.writeFileSync(args.csvOutputPath, stringify(tokens, csvOptions))

if (args.generateQrCodes) {
  console.log('QR kódok generálása...')
  generateQrCodes(tokens, args)
}
