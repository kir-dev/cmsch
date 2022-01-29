export const TIMESTAMP_OPTIONS: Intl.DateTimeFormatOptions = {
  month: 'short',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit'
}

export const stringifyTimeStamp = (timeStamp: number, options: Intl.DateTimeFormatOptions = TIMESTAMP_OPTIONS): string => {
  return new Date(timeStamp * 1000).toLocaleString('hu-HU', options)
}
