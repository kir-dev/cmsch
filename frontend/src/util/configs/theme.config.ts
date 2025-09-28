import { extendTheme } from '@chakra-ui/react'
import { mode } from '@chakra-ui/theme-tools'
import { Style } from '../../api/contexts/config/types.ts'
import { getColorShadesForColor } from '../core-functions.util.ts'

export const KirDevColor = '#F15A29'
const defaultBrand = {
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
}

export const getCustomTheme = (style?: Style) =>
  extendTheme(
    {
      styles: {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        global: (props: any) => ({
          body: {
            color: mode(style?.lightTextColor ?? 'gray.900', style?.darkTextColor ?? 'whiteAlpha.900')(props),
            bg: mode(style?.lightBackgroundColor ?? 'white', style?.darkBackgroundColor ?? 'gray.900')(props)
          }
        })
      },
      colors: {
        lightBrand: defaultBrand,
        darkBrand: defaultBrand,
        kirDev: '#F15A29'
      },
      components: {
        Heading: {
          baseStyle: {
            marginTop: 5
          }
        }
      }
    },
    getThemeExtensionFromStyle(style) || {}
  )

const getThemeExtensionFromStyle = (style?: Style) =>
  style && {
    colors: {
      brand: getColorShadesForColor(style.lightBrandingColor),
      lightBrand: getColorShadesForColor(style.lightBrandingColor),
      darkBrand: getColorShadesForColor(style.darkBrandingColor),
      lightContainerColor: getColorShadesForColor(style.lightContainerColor),
      lightContainerBg: style.lightContainerColor,
      darkContainerColor: getColorShadesForColor(style.darkContainerColor),
      darkContainerBg: style.darkContainerColor
    },
    fonts: {
      heading: style.mainFontName,
      body: style.mainFontName,
      display: style.displayFontName,
      mono: 'monospace'
    },
    components: {
      Heading: {
        variants: {
          'main-title': { fontFamily: style.displayFontName }
        }
      }
    }
  }
