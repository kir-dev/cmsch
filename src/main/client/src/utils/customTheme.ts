import { extendTheme } from '@chakra-ui/react'

// See more: https://chakra-ui.com/docs/theming/customize-theme
const customTheme = extendTheme({
  colors: {
    brand: {
      100: '#f7fafc',
      // ...
      900: '#1a202c'
    }
  }
})

export default customTheme
