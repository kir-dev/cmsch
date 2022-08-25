export const timestampToTimePassedStr = (timestamp: number | undefined) => {
  if (!timestamp) return ''
  const currentTime = (new Date(Date.now() + -new Date().getTimezoneOffset() * 60000).getTime() / 1000) | 0
  let elapsed = currentTime - timestamp
  let elapsedStr = elapsed.toFixed(0) + ' másodperce'

  if (elapsed > 120) {
    elapsed = elapsed / 60
    elapsedStr = elapsed.toFixed(0) + ' perce'
  }
  return `Utoljára frissítve: ${elapsedStr}`
}
