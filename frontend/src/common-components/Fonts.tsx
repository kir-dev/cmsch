/* eslint-disable max-len */
import { Global } from '@emotion/react'
import { useConfigContext } from '../api/contexts/config/ConfigContext'

export const Fonts = () => {
  const config = useConfigContext()
  return (
    <Global
      styles={`
      /* latin */
      @font-face {
        font-family: '${config?.components?.style?.displayFontName}}';
        font-style: normal;
        font-weight: ${config?.components?.style?.displayFontWeight};
        font-display: swap;
        src: url('${config?.components?.style?.displayFontCdn}') format('woff2'), url('./fonts/headingfont.woff') format('woff');
        unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
      }
      /* latin */
      @font-face {
        font-family: '${config?.components?.style?.mainFontName}}';
        font-style: normal;
        font-weight: ${config?.components?.style?.mainFontWeight};
        font-display: swap;
        src: url('${config?.components?.style?.mainFontCdn}') format('woff2'), url('./fonts/bodyfont.woff') format('woff');
        unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
      }
      `}
    />
  )
}
