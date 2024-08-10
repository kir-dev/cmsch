import fs from 'fs'
import puppeteer from 'puppeteer'
import Mime from 'mime'
import { padNumberToSameLength } from '../util.js'
import * as path from 'path'
import ProgressBar from 'progress'

const generateQrCode = async (page, { token, frontendUrl, qrImage, qrCodeStyling }) => {
  const data = `${frontendUrl}/token/scan?token=${token.token}`
  const renderOptions = { ...qrCodeStyling, data, image: qrImage }
  const result = await page.evaluate((renderOptions) => renderQrCode(renderOptions), renderOptions)
  if (!result.successful) {
    console.error(result.error)
    throw new Error('QR kód generálása sikertelen!')
  }
  return result
}

export const generateQrCodes = async (tokens, args) => {
  const { count, qrOutDirectory, frontendUrl, qrCodeStyling, qrRendererTemplate, qrImage } = args
  const bar = new ProgressBar(':bar :current/:total :percent', { total: count })
  const browser = await puppeteer.launch({ headless: true })
  try {
    const page = await browser.newPage()
    await page.setContent(fs.readFileSync(qrRendererTemplate, 'utf8'))

    for (const token of tokens) {
      const result = await generateQrCode(page, { token, frontendUrl, qrImage, qrCodeStyling })

      const qrName = padNumberToSameLength(token.index, count)
      const extension = Mime.getExtension(result.type)
      fs.writeFileSync(path.join(qrOutDirectory, `qr-${qrName}.${extension}`), Buffer.from(result.image))
      bar.tick()
    }
  } finally {
    await browser.close()
  }
}
