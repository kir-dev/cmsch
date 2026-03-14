import type { Style } from '@/api/contexts/config/types.ts'
import { createContext, type PropsWithChildren, useCallback, useContext, useEffect, useMemo, useState } from 'react'

interface ThemeContextType {
  persistentStyle: Style | undefined
  setPersistentStyle: (style?: Style) => void
  toggleColorMode: () => void
}

// eslint-disable-next-line react-refresh/only-export-components
export const PersistentStyleSettingContext = createContext<ThemeContextType>({
  persistentStyle: undefined,
  setPersistentStyle: () => {},
  toggleColorMode: () => {}
})

// eslint-disable-next-line react-refresh/only-export-components
export const usePersistentTheme = () => useContext(PersistentStyleSettingContext)

const StyleKey = 'persistent-style-setting'
const getPersistentStyle = () => {
  const styleJson = localStorage.getItem(StyleKey)
  if (!styleJson) return undefined
  return JSON.parse(styleJson) as Style
}

const savePersistentStyle = (value?: Style) => {
  if (!value) localStorage.removeItem(StyleKey)
  else localStorage.setItem(StyleKey, JSON.stringify(value))
}

const StyleInjector = ({ style }: { style?: Style }) => {
  useEffect(() => {
    if (!style) return
    const root = document.documentElement
    const lightText = style.lightTextColor || '#1a202c'
    const darkText = style.darkTextColor || '#ffffffeb'

    root.style.setProperty('--light-bg', style.lightBackgroundColor || '#ffffff')
    root.style.setProperty('--dark-bg', style.darkBackgroundColor || '#171923')
    root.style.setProperty('--light-text', lightText)
    root.style.setProperty('--dark-text', darkText)
    root.style.setProperty('--light-primary', style.lightBrandingColor || '#F15A29')
    root.style.setProperty('--dark-primary', style.darkBrandingColor || '#F15A29')
    root.style.setProperty('--light-primary-foreground', style.lightPrimaryForeground || '#ffffff')
    root.style.setProperty('--dark-primary-foreground', style.darkPrimaryForeground || '#ffffff')
    root.style.setProperty('--light-container-bg', style.lightContainerColor || '#ffffff')
    root.style.setProperty('--dark-container-bg', style.darkContainerColor || '#2d3748')
    root.style.setProperty('--main-font', style.mainFontName || 'inherit')
    root.style.setProperty('--display-font', style.displayFontName || style.mainFontName || 'inherit')

    root.style.setProperty('--light-border', style.lightBorderColor || '#e2e8f0')
    root.style.setProperty('--dark-border', style.darkBorderColor || '#2d3748')
    root.style.setProperty('--light-neutral', style.lightNeutralColor || '#edf2f7')
    root.style.setProperty('--dark-neutral', style.darkNeutralColor || '#2d3748')
    root.style.setProperty('--light-neutral-foreground', style.lightNeutralForeground || lightText)
    root.style.setProperty('--dark-neutral-foreground', style.darkNeutralForeground || darkText)
    root.style.setProperty('--light-success', style.lightSuccessColor || '#48bb78')
    root.style.setProperty('--dark-success', style.darkSuccessColor || '#68d391')
    root.style.setProperty('--light-success-foreground', style.lightSuccessForeground || '#ffffff')
    root.style.setProperty('--dark-success-foreground', style.darkSuccessForeground || '#1a202c')
    root.style.setProperty('--light-warning', style.lightWarningColor || '#ecc94b')
    root.style.setProperty('--dark-warning', style.darkWarningColor || '#faf089')
    root.style.setProperty('--light-warning-foreground', style.lightWarningForeground || '#ffffff')
    root.style.setProperty('--dark-warning-foreground', style.darkWarningForeground || '#1a202c')
    root.style.setProperty('--light-danger', style.lightDangerColor || '#f56565')
    root.style.setProperty('--dark-danger', style.darkDangerColor || '#fc8181')
    root.style.setProperty('--light-danger-foreground', style.lightDangerForeground || '#ffffff')
    root.style.setProperty('--dark-danger-foreground', style.darkDangerForeground || '#1a202c')
    root.style.setProperty('--light-info', style.lightInfoColor || '#4299e1')
    root.style.setProperty('--dark-info', style.darkInfoColor || '#63b3ed')
    root.style.setProperty('--light-info-foreground', style.lightInfoForeground || '#ffffff')
    root.style.setProperty('--dark-info-foreground', style.darkInfoForeground || '#1a202c')
  }, [style])
  return null
}

export const ThemeConfig = ({ children }: PropsWithChildren) => {
  const [persistentStyle, setPersistentStyle] = useState<Style | undefined>(getPersistentStyle())
  const [colorMode, setColorMode] = useState<'light' | 'dark'>(() => {
    const saved = localStorage.getItem('theme')
    if (saved === 'light' || saved === 'dark') return saved
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  })

  useEffect(() => {
    if (!persistentStyle) return
    const root = window.document.documentElement
    if (persistentStyle.forceDarkMode) {
      root.classList.add('dark')
    } else if (!persistentStyle.darkModeEnabled) {
      root.classList.remove('dark')
    } else {
      if (colorMode === 'dark') {
        root.classList.add('dark')
      } else {
        root.classList.remove('dark')
      }
    }
  }, [persistentStyle, colorMode])

  const toggleColorMode = useCallback(() => {
    setColorMode((prev) => {
      const next = prev === 'dark' ? 'light' : 'dark'
      localStorage.setItem('theme', next)
      return next
    })
  }, [])

  const contextData = useMemo(
    () => ({
      persistentStyle,
      setPersistentStyle: (style?: Style) => {
        savePersistentStyle(style)
        setPersistentStyle(style)
      },
      toggleColorMode
    }),
    [persistentStyle, toggleColorMode]
  )

  return (
    <PersistentStyleSettingContext.Provider value={contextData}>
      <StyleInjector style={persistentStyle} />
      {children}
    </PersistentStyleSettingContext.Provider>
  )
}
