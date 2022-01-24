import { extendTheme } from '@chakra-ui/react'
import { mode } from '@chakra-ui/theme-tools'

// See more: https://chakra-ui.com/docs/theming/customize-theme
const customTheme = extendTheme({
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
      // generated here: https://maketintsandshades.com/#80C500
      50: '#f2f9e6',
      100: '#d9eeb3',
      200: '#c0e280',
      300: '#a6d64d',
      400: '#8dcb1a',
      500: '#80c500',
      600: '#669e00',
      700: '#4d7600',
      800: '#334f00',
      900: '#1a2700'
    },
    brand2: {
      // generated here: https://maketintsandshades.com/#FAA916
      50: '#fff6e8',
      100: '#fee5b9',
      200: '#fdd48b',
      300: '#fcc35c',
      400: '#fbb22d',
      500: '#faa916',
      600: '#c88712',
      700: '#96650d',
      800: '#644409',
      900: '#322204'
    },
    gray: {
      // generated here: https://maketintsandshades.com/#78716c
      50: '#f2f1f0',
      100: '#d7d4d3',
      200: '#bcb8b6',
      300: '#a19c98',
      400: '#867f7b',
      500: '#78716c',
      600: '#605a56',
      700: '#484441',
      800: '#302d2b',
      900: '#1d1c1b'
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
