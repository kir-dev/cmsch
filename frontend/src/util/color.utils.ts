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

export function calculateRelativeLuminance([r, g, b]: [number, number, number]): number {
  const RsRGB = r / 255
  const GsRGB = g / 255
  const BsRGB = b / 255

  const R = RsRGB <= 0.03928 ? RsRGB / 12.92 : Math.pow((RsRGB + 0.055) / 1.055, 2.4)
  const G = GsRGB <= 0.03928 ? GsRGB / 12.92 : Math.pow((GsRGB + 0.055) / 1.055, 2.4)
  const B = BsRGB <= 0.03928 ? BsRGB / 12.92 : Math.pow((BsRGB + 0.055) / 1.055, 2.4)

  return 0.2126 * R + 0.7152 * G + 0.0722 * B
}

export function calculateContrastRatio(l1: number, l2: number): number {
  return l1 >= l2 ? (l1 + 0.05) / (l2 + 0.05) : (l2 + 0.05) / (l1 + 0.05)
}

export function isContrastEnough(hexColor: string): boolean {
  const [r, g, b] = hexToRgb(hexColor)
  const luminance = calculateRelativeLuminance([r, g, b])
  const whiteLuminance = calculateRelativeLuminance([255, 255, 255])
  const contrastRatio = calculateContrastRatio(luminance, whiteLuminance)

  return contrastRatio >= 4.5
}
