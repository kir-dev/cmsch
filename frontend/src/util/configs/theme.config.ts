import { extendTheme } from '@chakra-ui/react'
import { mode } from '@chakra-ui/theme-tools'
import { Style } from '../../api/contexts/config/types.ts'
import { getColorShadesForColor } from '../core-functions.util.ts'

const defaultBrandColor = '#F15A29'
const defaultBorderColor = '#718096'
const defaultSuccessColor = '#38A169'
const defaultWarningColor = '#38A169'
const defaultInfoColor = '#3182ce'
const defaultErrorColor = '#E53E3E'

const paletteModeWithDefault = (light: string | undefined, dark: string | undefined, defaultColor: string, modeProps: any) =>
  mode(getColorShadesForColor(light), getColorShadesForColor(dark))(modeProps) || defaultColor

// See more: https://chakra-ui.com/docs/theming/customize-theme
export const createCustomTheme = (colorMode: any, style?: Style) =>
  extendTheme({
    styles: {
      global: (props: any) => ({
        body: {
          color: mode(style?.lightTextColor || 'border.900', style?.darkTextColor || 'whiteAlpha.900')(props),
          bg: mode(style?.lightBackgroundColor || 'white', style?.darkBackgroundColor || 'border.900')(props)
        }
      })
    },
    colors: {
      ...(style && {
        lightContainerColor: getColorShadesForColor(style.lightContainerColor),
        lightContainerBg: style.lightContainerColor,
        darkContainerColor: getColorShadesForColor(style.darkContainerColor),
        darkContainerBg: style.darkContainerColor
      }),
      border: paletteModeWithDefault(style?.lightBorderColor, style?.darkBorderColor, defaultBorderColor, colorMode),
      success: paletteModeWithDefault(style?.lightSuccessColor, style?.darkSuccessColor, defaultSuccessColor, colorMode),
      warning: paletteModeWithDefault(style?.lightWarningColor, style?.darkWarningColor, defaultWarningColor, colorMode),
      info: paletteModeWithDefault(style?.lightInfoColor, style?.darkInfoColor, defaultInfoColor, colorMode),
      error: paletteModeWithDefault(style?.lightErrorColor, style?.darkErrorColor, defaultErrorColor, colorMode),
      brandForeground: paletteModeWithDefault(style?.lightBrandForeground, style?.darkBrandForeground, defaultErrorColor, colorMode),
      brand: paletteModeWithDefault(style?.lightBrandingColor, style?.darkBrandingColor, defaultBrandColor, colorMode),
      text: mode(style?.lightTextColor || 'border.900', style?.darkTextColor || 'whiteAlpha.900')(colorMode),
      kirDev: '#F15A29'
    },
    fonts: {
      ...(style && {
        heading: style.mainFontName,
        body: style.mainFontName,
        display: style.displayFontName
      }),
      mono: 'monospace'
    },
    components: {
      Text: {
        baseStyle: (props: any) => ({
          color: mode(style?.lightTextColor, style?.darkTextColor)(props)
        }),
        variants: {
          secondary: (props: any) => ({
            color: mode(style?.lightTextColor, style?.darkTextColor)(props)
          })
        }
      },
      Icon: {
        baseStyle: (props: any) => ({
          color: mode(style?.lightTextColor, style?.darkTextColor)(props)
        }),
        variants: {
          secondary: (props: any) => ({
            color: mode(style?.lightTextColor, style?.darkTextColor)(props)
          })
        }
      },
      Link: {
        baseStyle: (props: any) => ({
          color: mode(style?.lightTextColor, style?.darkTextColor)(props)
        }),
        variants: {
          secondary: (props: any) => ({
            color: mode(style?.lightTextColor, style?.darkTextColor)(props)
          })
        }
      },
      Heading: {
        baseStyle: (props: any) => ({
          color: mode(style?.lightTextColor, style?.darkTextColor)(props),
          marginTop: 5
        }),
        variants: {
          'main-title': (props: any) => ({
            color: mode(style?.lightTextColor, style?.darkTextColor)(props),
            ...(style && { fontFamily: style.displayFontName })
          })
        }
      }
    }
  })
