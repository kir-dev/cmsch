import { createCustomTheme } from '../../../util/configs/theme.config'
import { useConfigContext } from '../config/ConfigContext'
import { ChakraProvider, useColorMode } from '@chakra-ui/react'
import { PropsWithChildren, useEffect, useMemo } from 'react'

export const BaseTheme = ({ children }: PropsWithChildren) => {
  const { colorMode } = useColorMode()
  const chakraConfig = useMemo(() => createCustomTheme({ colorMode }), [colorMode])
  return <ChakraProvider theme={chakraConfig}>{children}</ChakraProvider>
}

export const ThemeConfig = ({ children }: PropsWithChildren) => {
  const style = useConfigContext()?.components?.style
  const { colorMode, setColorMode } = useColorMode()

  const deviceTheme = style?.deviceTheme
  const forceDarkMode = style?.forceDarkMode
  useEffect(() => {
    setColorMode((deviceTheme && 'system') || (forceDarkMode && 'dark') || 'light')
  }, [deviceTheme, forceDarkMode])

  const chakraConfig = useMemo(() => createCustomTheme({ colorMode }, style), [style, colorMode])
  return <ChakraProvider theme={chakraConfig}>{children}</ChakraProvider>
}
