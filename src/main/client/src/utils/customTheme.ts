import { extendTheme } from '@chakra-ui/react'
import { mode } from '@chakra-ui/theme-tools'

// See more: https://chakra-ui.com/docs/theming/customize-theme
const customTheme = extendTheme({
  fonts: {
    heading: 'Commissioner',
    body: 'Roboto'
  },
  styles: {
    global: (props: any) => ({
      body: {
        color: mode('gray.800', 'whiteAlpha.900')(props),
        bg: mode('white', 'gray.900')(props)
      }
    })
  },
  colors: {
    brand: {
      100: '#d4f3e7',
      200: '#a8e7ce',
      300: '#7ddcb6',
      400: '#51d09d',
      500: '#26c485',
      600: '#1e9d6a',
      700: '#177650',
      800: '#0f4e35',
      900: '#08271b'
    }
  },
  components: {
    Heading: {
      baseStyle: {
        marginTop: 5
      }
    },
    Text: {
      baseStyle: {
        fontSize: 'lg'
      }
    }
  }
})

export default customTheme
