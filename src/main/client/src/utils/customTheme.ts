import { extendTheme } from '@chakra-ui/react'
import { mode, createBreakpoints } from '@chakra-ui/theme-tools'

// See more: https://chakra-ui.com/docs/theming/customize-theme
const customTheme = extendTheme({
  breakpoints: createBreakpoints({
    sm: '30em',
    md: '48em',
    lg: '68em',
    xl: '80em'
  }),
  styles: {
    global: (props: any) => ({
      body: {
        color: mode('gray.900', 'whiteAlpha.900')(props),
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
