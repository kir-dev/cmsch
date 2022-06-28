import { HasChildren } from '../../../util/react-types.util'
import { customTheme } from '../../../util/configs/theme.config'
import { useConfigContext } from '../config/ConfigContext'
import { ChakraProvider } from '@chakra-ui/react'
import Values from 'values.js'
import { useMemo } from 'react'
import { mode } from '@chakra-ui/theme-tools'
import { Global } from '@emotion/react'

export const ThemeConfig = ({ children }: HasChildren) => {
  const config = useConfigContext()
  const chakraConfig = useMemo(() => {
    let Fonts = null
    if (config) {
      customTheme.colors.brand = getColorShadesForColor(config.components.style.brandingColor)
      customTheme.colors.accent = config.components.style.textColorAccent
      const bgColor = new Values(config.components.style.backgroundColor)
      const textColor = new Values(config.components.style.textColor)
      const backgrounds = getOppositeColor(bgColor, 90)
      const texts = getOppositeColor(textColor, 1000)
      customTheme.styles.global = (props: any) => ({
        body: {
          bg: mode(backgrounds.light.hexString(), backgrounds.dark.hexString())(props),
          color: mode(texts.dark.hexString(), texts.light.hexString())(props)
        }
      })
      customTheme.fonts = {
        heading: config.components.style.mainFontName,
        display: config.components.style.displayFontName
      }

      Fonts = () => (
        <Global
          styles={`
            /* latin */
            @font-face {
              font-family: ${config.components.style.mainFontName};
              font-style: normal;
              font-weight: ${config.components.style.mainFontWeight};
              font-display: swap;
              src: url(${config.components.style.mainFontCdn}) format('woff2'), url(${config.components.style.mainFontCdn}) format('woff');
              unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074,
               U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
            }
            /* latin */
            @font-face {
              font-family: 'display';
              font-style: normal;
              font-weight: ${config.components.style.displayFontWeight};
              font-display: swap;
              src: url(${config.components.style.displayFontCdn}) format('woff2'),
                url(${config.components.style.displayFontCdn}) format('woff');
              unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074,
              U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
            }
            `}
        />
      )
    }
    return { theme: customTheme, fonts: Fonts }
  }, [config])
  return (
    <ChakraProvider theme={chakraConfig.theme}>
      <>
        {chakraConfig.fonts}
        {children}
      </>
    </ChakraProvider>
  )
}

function getColorShadesForColor(color: string) {
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

function getOppositeColor(color: Values, weight: number): { light: Values; dark: Values } {
  if (color.getBrightness() < 50) {
    return { light: color.tint(weight), dark: color }
  } else {
    return { light: color, dark: color.shade(weight) }
  }
}
