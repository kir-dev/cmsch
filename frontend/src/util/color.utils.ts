import Color, { ColorInstance } from 'color'

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

export function rgbToHex(r: number, g: number, b: number): string {
  const toHex = (n: number) => {
    return Math.round(n).toString(16).padStart(2, '0')
  }

  return `#${toHex(r)}${toHex(g)}${toHex(b)}`
}

export function isValidHex(hex: string): boolean {
  return /^#([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{9})$/.test(hex)
}

export function useColor(hex: string | undefined): ColorInstance {
  try {
    return Color(hex)
  } catch {
    return Color('#FF9900')
  }
}

export function getTextColorFromLuminance(backgroundColor: string) {
  const [r, g, b] = hexToRgb(backgroundColor)
  return (0.299 * r + 0.587 * g + 0.114 * b) / 255 > 0.5 ? '#000' : '#FFF'
}
