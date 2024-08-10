import Mime from 'mime'
import fs from 'fs'

export const fileToBase64 = (fileName) => {
  const mediaType = Mime.getType(fileName)
  const data = fs.readFileSync(fileName).toString('base64')

  return `data:${mediaType};base64,${data}`
}

export const padNumberToSameLength = (number, reference) => number.toString().padStart(reference.toString().length, '0')
