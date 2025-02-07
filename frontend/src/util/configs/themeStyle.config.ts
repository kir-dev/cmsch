import { Style } from '../../api/contexts/config/types.ts'
import { createContext, useContext } from 'react'

export type PersistentStyleSettingData = {
  persistentStyle?: Style
  setPersistentStyle: (style?: Style) => void
}

export const PersistentStyleSettingContext = createContext<PersistentStyleSettingData>({
  persistentStyle: undefined,
  setPersistentStyle: () => {}
})

export const usePersistentStyleSetting = () => useContext(PersistentStyleSettingContext)

const StyleKey = 'persistent-style-setting'
export const getPersistentStyle = () => {
  const styleJson = localStorage.getItem(StyleKey)
  if (!styleJson) return undefined
  return JSON.parse(styleJson) as Style
}

export const savePersistentStyle = (value?: Style) => {
  if (!value) localStorage.removeItem(StyleKey)
  else localStorage.setItem(StyleKey, JSON.stringify(value))
}
