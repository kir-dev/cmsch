export function hexToRgb(hex: string): [number, number, number] {
  // Remove the hash if it exists
  hex = hex.replace(/^#/, '')

  // Parse the color components
  const bigint = parseInt(hex, 16)
  const r = (bigint >> 16) & 255
  const g = (bigint >> 8) & 255
  const b = bigint & 255

  return [r, g, b]
}

export function getTextColorFromLuminance(backgroundColor: string) {
  const [r, g, b] = hexToRgb(backgroundColor)
  return (0.299 * r + 0.587 * g + 0.114 * b) / 255 > 0.5 ? '#000' : '#FFF'
}
