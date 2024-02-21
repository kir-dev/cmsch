import { customTheme } from '../../../util/configs/theme.config'
import { useConfigContext } from '../config/ConfigContext'
import { ChakraProvider, useColorMode } from '@chakra-ui/react'
import { PropsWithChildren, useMemo } from 'react'
import { mode } from '@chakra-ui/theme-tools'
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
        body: config.components.style.mainFontName,
        mono: 'monospace'
      }
    }
    return customTheme
  }, [config])
  return <ChakraProvider theme={chakraConfig}>{children}</ChakraProvider>
}
