import { customTheme } from '../../../util/configs/theme.config'
import { useConfigContext } from '../config/ConfigContext'
import { ChakraProvider, useColorMode } from '@chakra-ui/react'
import { PropsWithChildren, useEffect, useMemo } from 'react'
import { getColorShadesForColor } from '../../../util/core-functions.util'
import { Style } from '../config/types.ts'

export const ThemeConfig = ({ children }: PropsWithChildren) => {
  const config = useConfigContext()

  useThemeUpdate(config?.components?.style)
  const chakraConfig = useMemo(() => {
    if (config?.components.style) {
      customTheme.colors.brand = getColorShadesForColor(config.components.style.lightBrandingColor)
      customTheme.colors.lightContainerColor = getColorShadesForColor(config.components.style.lightContainerColor)
      customTheme.colors.lightContainerBg = config.components.style.lightContainerColor
      customTheme.colors.darkContainerColor = getColorShadesForColor(config.components.style.darkContainerColor)
      customTheme.colors.darkContainerBg = config.components.style.darkContainerColor
      customTheme.fonts = {
        heading: config.components.style.mainFontName,
        body: config.components.style.mainFontName,
        display: config.components.style.displayFontName,
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

const useThemeUpdate = (style?: Style) => {
  const { colorMode, setColorMode } = useColorMode()
  useEffect(() => {
    if (!style) return
    if (colorMode !== 'dark' && style.forceDarkMode) {
      setColorMode('dark')
    } else if (!style.darkModeEnabled) setColorMode('white')
  }, [!!style, style?.deviceTheme, style?.forceDarkMode])
}
