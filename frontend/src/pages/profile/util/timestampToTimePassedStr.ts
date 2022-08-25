export const timestampToTimePassedStr = (timestamp: number | undefined) => {
  if (!timestamp) return ''
  const currentTime = Date.now() / 1000
  let elapsed = currentTime - timestamp
  let elapsedStr = elapsed.toFixed(0) + ' másodperce'

  if (elapsed > 120) {
    elapsed = elapsed / 60
    elapsedStr = elapsed.toFixed(0) + ' perce'
  }
  return `Utoljára frissítve: ${elapsedStr}`
}
