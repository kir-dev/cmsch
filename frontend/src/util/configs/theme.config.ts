import { extendTheme } from '@chakra-ui/react'
import { mode } from '@chakra-ui/theme-tools'

// See more: https://chakra-ui.com/docs/theming/customize-theme
export let customTheme = extendTheme({
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
      50: '#d9fae7',
      100: '#fcded4',
      200: '#f9bda9',
      300: '#f79c7f',
      400: '#f47b54',
      500: '#F15A29',
      600: '#c14821',
      700: '#913619',
      800: '#602410',
      900: '#301208'
    },
    kirDev: '#F15A29'
  },
  components: {
    Heading: {
      baseStyle: {
        marginTop: 5
      }
    }
  }
})
