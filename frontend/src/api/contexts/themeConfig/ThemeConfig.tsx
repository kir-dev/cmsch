import { ChakraProvider, useColorMode } from '@chakra-ui/react'
import { PropsWithChildren, useEffect, useMemo, useState } from 'react'
import { Style } from '../config/types.ts'
import { getCustomTheme } from '../../../util/configs/theme.config.ts'
import { getPersistentStyle, PersistentStyleSettingContext, savePersistentStyle } from '../../../util/configs/themeStyle.config.ts'

export const ThemeConfig = ({ children }: PropsWithChildren) => {
  const [persistentStyle, setPersistentStyle] = useState<Style | undefined>(getPersistentStyle())

  const { colorMode, setColorMode } = useColorMode()
  useEffect(() => {
    if (!persistentStyle) return
    if (colorMode !== 'dark' && persistentStyle.forceDarkMode) {
      setColorMode('dark')
    } else if (!persistentStyle.darkModeEnabled) setColorMode('white')
  }, [!!persistentStyle, persistentStyle?.deviceTheme, persistentStyle?.forceDarkMode])

  const theme = useMemo(() => getCustomTheme(persistentStyle), [persistentStyle])
  const contextData = useMemo(
    () => ({
      persistentStyle,
      setPersistentStyle: (style?: Style) => {
        savePersistentStyle(style)
        setPersistentStyle(style)
      }
    }),
    [persistentStyle]
  )

  return (
    <PersistentStyleSettingContext.Provider value={contextData}>
      <ChakraProvider theme={theme}>{children}</ChakraProvider>
    </PersistentStyleSettingContext.Provider>
  )
}
