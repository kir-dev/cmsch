import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { usePersistentTheme } from '@/api/contexts/themeConfig/ThemeConfig'
import { useIsLightMode } from '@/util/core-functions.util'
import { Moon, Sun } from 'lucide-react'

export const ColorModeSwitcher = ({ color }: { color?: string }) => {
  const { toggleColorMode } = usePersistentTheme()
  const config = useConfigContext()
  const isLightMode = useIsLightMode()

  if (!config?.components?.style?.darkModeEnabled) return null

  return (
    <button
      aria-label="Sötét-világos mód váltás"
      onClick={toggleColorMode}
      className="p-2 rounded-md hover:bg-accent transition-colors"
      style={{ color }}
    >
      {isLightMode ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}{' '}
    </button>
  )
}
