import { HasChildren } from '../../../util/react-types.util'
import { customTheme } from '../../../util/configs/theme.config'
import { useConfigContext } from '../config/ConfigContext'
import { ChakraProvider, useColorMode } from '@chakra-ui/react'
import Values from 'values.js'
import { useMemo } from 'react'
import { mode } from '@chakra-ui/theme-tools'
import { Helmet } from 'react-helmet-async'

export const ThemeConfig = ({ children }: HasChildren) => {
  const config = useConfigContext()
  const { setColorMode } = useColorMode()

  const chakraConfig = useMemo(() => {
    if (config?.components.style) {
      customTheme.colors.brand = getColorShadesForColor(config.components.style.lightBrandingColor)
      customTheme.colors.lightContainerBg = config.components.style.lightContainerColor
      customTheme.colors.darkContainerBg = config.components.style.darkContainerColor
      setColorMode((config.components.style.deviceTheme && 'system') || (config.components.style.forceDarkMode && 'dark') || 'light')
      const defaultGlobal = customTheme.styles.global
      customTheme.styles.global = (props: any) => ({
        ...defaultGlobal(props),
        body: {
          bg: mode(config.components.style.lightBackgroundColor, config.components.style.darkBackgroundColor)(props),
          color: mode(config.components.style.lightTextColor, config.components.style.darkTextColor)(props),
          bgImage: mode(`url(${config.components.style.lightBackgroundUrl})`, `url(${config.components.style.darkBackgroundUrl})`)(props),
          bgRepeat: 'no-repeat',
          bgSize: 'cover',
          bgPosition: 'center',
          bgAttachment: 'fixed',
          [`@media screen and (max-width: ${props.theme.breakpoints.sm})`]: {
            bgImage: mode(
              `url(${config.components.style.lightMobileBackgroundUrl})`,
              `url(${config.components.style.darkMobileBackgroundUrl})`
            )
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
      {(config?.components.style.mainFontCdn || config?.components.style.displayFontCdn) && (
        <Helmet>
          <link href={config?.components.style.displayFontCdn} rel="stylesheet" />
          <link href={config?.components.style.mainFontCdn} rel="stylesheet" />
        </Helmet>
      )}
      {children}
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
