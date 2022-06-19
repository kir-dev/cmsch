import { HasChildren } from '../../../util/react-types.util'
import { customTheme } from '../../../util/configs/theme.config'
import { useConfigContext } from '../config/ConfigContext'
import { ChakraProvider } from '@chakra-ui/react'
import Values from 'values.js'
import { useMemo } from 'react'

export const ThemeConfig = ({ children }: HasChildren) => {
  const config = useConfigContext()
  // TODO: Should overwrite more fields if API supports it
  const chakraConfig = useMemo(() => {
    if (config) customTheme.colors.brand = getColorShadesForColor(config.components.style.brandingColor)
    return customTheme
  }, [config])
  return <ChakraProvider theme={chakraConfig}>{children}</ChakraProvider>
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
