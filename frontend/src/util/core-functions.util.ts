import { format, formatDistance } from 'date-fns'
import Values from 'values.js'

export const toReadableNumber = (num: number): string =>
  Intl.NumberFormat('en-US', {
    notation: 'compact',
    maximumFractionDigits: 1
  }).format(num)

export const toRelativeDateString = (timestamp: number): string =>
  formatDistance(new Date(timestamp), new Date(), { includeSeconds: true, addSuffix: true })

export const DEFAULT_DATE_OPTIONS = { year: 'numeric', month: 'long', day: 'numeric' }
export const LONGER_DATE_OPTIONS = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }

export const toDateString = (timestamp: number, options: object = DEFAULT_DATE_OPTIONS): string =>
  new Date(timestamp).toLocaleDateString('en-US', options)

export const toDateTimeString = (timestamp: number): string => format(new Date(timestamp), 'MMMM dd, yyyy - HH:mm:ss')

export const ellipsifyLongText = (text: string, maxLength: number = 100): string =>
  text.substring(0, maxLength - 1) + (text.length > maxLength - 1 ? '...' : '')

export const TIMESTAMP_OPTIONS: Intl.DateTimeFormatOptions = {
  month: 'short',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit'
}

export const DETAILED_TIMESTAMP_OPTIONS: Intl.DateTimeFormatOptions = {
  month: 'short',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  weekday: 'long'
}

export const GROUP_BY_DAY_OPTIONS: Intl.DateTimeFormatOptions = {
  month: 'short',
  day: '2-digit',
  weekday: 'long'
}

export const HOUR_AND_MIN_OPTIONS: Intl.DateTimeFormatOptions = {
  hour: '2-digit',
  minute: '2-digit'
}

export const stringifyTimeStamp = (timeStamp: number, options: Intl.DateTimeFormatOptions = TIMESTAMP_OPTIONS): string => {
  return new Date(timeStamp * 1000).toLocaleString('hu-HU', options)
}

export const stringifyTimeRange = (fromTimeStamp: number, toTimeStamp: number) => {
  const from = new Date(fromTimeStamp * 1000)
  const to = new Date(toTimeStamp * 1000)
  if (from.getDate() === to.getDate() && from.getMonth() === from.getMonth()) {
    return `${from.toLocaleString('hu-HU', GROUP_BY_DAY_OPTIONS)} ${from.toLocaleString(
      'hu-HU',
      HOUR_AND_MIN_OPTIONS
    )} - ${to.toLocaleString('hu-HU', HOUR_AND_MIN_OPTIONS)}`
  } else {
    return `${from.toLocaleString('hu-HU', TIMESTAMP_OPTIONS)} - ${to.toLocaleString('hu-HU', TIMESTAMP_OPTIONS)}`
  }
}

export function getColorShadesForColor(color: string) {
  const colors = new Values(color)
  const tints = colors.tints(21).reverse()
  const shades = colors.shades(21)
  let result: Record<number, string> = {}
  tints.forEach((t, i) => {
    result[(i + 1) * 100] = t.hexString()
  })
  result[500] = color
  shades.forEach((t, i) => {
    result[(i + 6) * 100] = t.hexString()
  })
  return result
}
