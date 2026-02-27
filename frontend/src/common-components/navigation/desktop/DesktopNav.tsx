import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { NavItemNoChildren } from './NavItemNoChildren'
import { NavItemWithChildren } from './NavItemWithChildren'

export const DesktopNav = () => {
  const config = useConfigContext()

  if (!config?.menu) return null

  return (
    <div className="flex flex-row space-x-4">
      {config.menu.map((menu) =>
        menu.children && menu.children.length > 0 ? (
          <NavItemWithChildren key={menu.name} menu={menu} />
        ) : (
          <NavItemNoChildren key={menu.name} menu={menu} />
        )
      )}
    </div>
  )
}
