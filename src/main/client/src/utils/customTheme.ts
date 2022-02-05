import { extendTheme } from '@chakra-ui/react'
import { mode, SystemStyleObject } from '@chakra-ui/theme-tools'

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
      100: '#d4f3e7',
      200: '#a8e7ce',
      300: '#7ddcb6',
      400: '#51d09d',
      500: '#26c485',
      600: '#1e9d6a',
      700: '#177650',
      800: '#0f4e35',
      900: '#08271b'
    },
    kirDev: '#F15A29'
  },
  components: {
    Heading: {
      baseStyle: {
        marginTop: 5
      }
    },
    Text: {
      baseStyle: {
        // fontSize: 'lg'
      }
    },
    Button: {
      variants: {
        solid: (props: SystemStyleObject) => {
          const { colorScheme: c } = props
          if (c === 'gray') {
            const bg = mode(`gray.100`, `whiteAlpha.200`)(props)
            return {
              bg,
              _hover: {
                bg: mode(`gray.200`, `whiteAlpha.300`)(props),
                _disabled: {
                  bg
                }
              },
              _active: { bg: mode(`gray.300`, `whiteAlpha.400`)(props) }
            }
          }
          const yellowOrCyan = c === 'yellow' || c === 'cyan'
          const bg = yellowOrCyan ? `${c}.400` : `${c}.500`
          const color = yellowOrCyan ? 'black' : 'white'
          const hoverBg = yellowOrCyan ? `${c}.500` : `${c}.600`
          const activeBg = yellowOrCyan ? `${c}.600` : `${c}.700`

          const background = mode(bg, `${c}.700`)(props)
          return {
            bg: background,
            color: mode(color, `whiteAlpha.900`)(props),
            _hover: {
              bg: mode(hoverBg, `${c}.600`)(props),
              _disabled: {
                bg: background
              }
            },
            _active: { bg: mode(activeBg, `${c}.600`)(props) }
          }
        }
      }
    }
  }
})

export default customTheme
