import { HasChildren } from '../../../util/react-types.util'
import { customTheme } from '../../../util/configs/theme.config'
import { useConfigContext } from '../config/ConfigContext'
import { ChakraProvider } from '@chakra-ui/react'
import Values from 'values.js'
import { useMemo } from 'react'
import { mode } from '@chakra-ui/theme-tools'
import { Helmet } from 'react-helmet'

export const ThemeConfig = ({ children }: HasChildren) => {
  const config = useConfigContext()
  const chakraConfig = useMemo(() => {
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
          color: mode(texts.dark.hexString(), texts.light.hexString())(props),
          bgImage: `url(${config.components.style.backgroundUrl})`,
          [`@media screen and (max-width: ${props.theme.breakpoints.sm})`]: {
            bgImage: `url(${config.components.style.mobileBackgroundUrl})`
          }
        }
      })
      customTheme.fonts = {
        heading: config.components.style.displayFontName,
        body: config.components.style.mainFontName
      }
    }
    return customTheme
  }, [config])
  return (
    <ChakraProvider theme={chakraConfig}>
      <>
        {(config?.components.style.mainFontCdn || config?.components.style.displayFontCdn) && (
          <Helmet>
            <link rel="preconnect" href="https://fonts.googleapis.com" />
            <link rel="preconnect" href="https://fonts.gstatic.com" />
            <link href={config?.components.style.displayFontCdn} rel="stylesheet" />
            <link href={config?.components.style.mainFontCdn} rel="stylesheet" />
          </Helmet>
        )}
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
