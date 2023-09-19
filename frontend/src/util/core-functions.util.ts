import { useColorModeValue } from '@chakra-ui/react'
import { format } from 'date-fns'
import { hu } from 'date-fns/locale'
import Values from 'values.js'
import { API_BASE_URL } from './configs/environment.config'
import { FormFieldVariants } from './views/form.view'

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
  const from = new Date(fromTimeStamp)
  const to = new Date(toTimeStamp)
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

export function joinPath(...parts: (string | number | undefined)[]) {
  return parts.filter(Boolean).join('/')
}

export function isCheckbox(type: FormFieldVariants) {
  return type === FormFieldVariants.CHECKBOX || type === FormFieldVariants.MUST_AGREE
}

export function getCdnUrl(path: string) {
  return joinPath(API_BASE_URL, 'cdn', path)
}

export function isCurrentEvent(event: { timestampStart: number; timestampEnd: number }) {
  const now = new Date().getTime() / 1000
  return event.timestampStart <= now && event.timestampEnd >= now
}

export function isUpcomingEvent(event: { timestampStart: number; timestampEnd: number }) {
  const now = new Date().getTime() / 1000
  const diff = event.timestampStart - now
  return diff > 0 && diff < 3600
}

export function formatHu(date: Date | number, formatString: string) {
  return format(date, formatString, { locale: hu })
}

export function useOpaqueBackground(intensity: number = 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10) {
  const portion = 10
  let intensityHex = Math.round(intensity * portion).toString(16)
  if (intensityHex.length === 1) {
    intensityHex = '0' + intensityHex
  }
  return useColorModeValue('#000000' + intensityHex, '#FFFFFF' + intensityHex)
}
