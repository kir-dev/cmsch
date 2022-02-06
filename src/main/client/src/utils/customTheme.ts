import { extendTheme } from '@chakra-ui/react'
import { alertAnatomy as parts } from '@chakra-ui/anatomy'
import { PartsStyleFunction, mode, SystemStyleFunction, transparentize } from '@chakra-ui/theme-tools'

// functions copied from https://github.com/chakra-ui/chakra-ui/blob/main/packages/theme/src/components/button.ts
// (and alert.ts) then modified to our liking
const buttonVariantGhost: SystemStyleFunction = (props) => {
  const { colorScheme: c, theme } = props

  if (c === 'gray') {
    return {
      color: mode(`inherit`, `whiteAlpha.900`)(props),
      _hover: {
        bg: mode(`gray.100`, `whiteAlpha.200`)(props)
      },
      _active: { bg: mode(`gray.200`, `whiteAlpha.300`)(props) }
    }
  }

  const darkHoverBg = transparentize(`${c}.500`, 0.12)(theme)
  const darkActiveBg = transparentize(`${c}.500`, 0.24)(theme)

  return {
    color: mode(`${c}.500`, `${c}.600`)(props),
    bg: 'transparent',
    _hover: {
      bg: mode(`${c}.50`, darkHoverBg)(props)
    },
    _active: {
      bg: mode(`${c}.100`, darkActiveBg)(props)
    }
  }
}

const buttonVariantSolid: SystemStyleFunction = (props) => {
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

const buttonVariantOutline: SystemStyleFunction = (props) => {
  const { colorScheme: c } = props
  const borderColor = mode(`gray.200`, `whiteAlpha.300`)(props)
  return {
    border: '1px solid',
    borderColor: c === 'gray' ? borderColor : 'currentColor',
    ...buttonVariantGhost(props)
  }
}

const alertVariantSolid: PartsStyleFunction<typeof parts> = (props) => {
  const { colorScheme: c } = props
  return {
    container: {
      bg: mode(`${c}.500`, `${c}.600`)(props),
      color: 'white'
    }
  }
}

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
      50: '#d9fae7',
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
        solid: buttonVariantSolid,
        ghost: buttonVariantGhost,
        outline: buttonVariantOutline
      }
    },
    Alert: {
      variants: {
        solid: alertVariantSolid
      }
    }
  }
})

export default customTheme
