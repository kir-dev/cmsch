import { customTheme } from '../../../util/configs/theme.config'
import { useConfigContext } from '../config/ConfigContext'
import { ChakraProvider, useColorMode } from '@chakra-ui/react'
import { PropsWithChildren, useMemo } from 'react'
import { getColorShadesForColor } from '../../../util/core-functions.util'

export const ThemeConfig = ({ children }: PropsWithChildren) => {
  const config = useConfigContext()
  const { setColorMode } = useColorMode()

  const chakraConfig = useMemo(() => {
    if (config?.components.style) {
      customTheme.colors.brand = getColorShadesForColor(config.components.style.lightBrandingColor)
      customTheme.colors.lightContainerColor = getColorShadesForColor(config.components.style.lightContainerColor)
      customTheme.colors.lightContainerBg = config.components.style.lightContainerColor
      customTheme.colors.darkContainerColor = getColorShadesForColor(config.components.style.darkContainerColor)
      customTheme.colors.darkContainerBg = config.components.style.darkContainerColor
      setColorMode((config.components.style.deviceTheme && 'system') || (config.components.style.forceDarkMode && 'dark') || 'light')
      customTheme.fonts = {
        heading: config.components.style.mainFontName,
        body: config.components.style.mainFontName,
        mono: 'monospace'
      }
      customTheme.components.Heading = {
        ...customTheme.components.Heading,
        variants: {
          'main-title': { fontFamily: config.components.style.displayFontName }
        }
      }
    }
    return customTheme
  }, [config])
  return <ChakraProvider theme={chakraConfig}>{children}</ChakraProvider>
}
