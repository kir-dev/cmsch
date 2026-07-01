import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { NavigationMenu, NavigationMenuList } from '@/components/ui/navigation-menu'
import { NavItemNoChildren } from './NavItemNoChildren'
import { NavItemWithChildren } from './NavItemWithChildren'

export const DesktopNav = () => {
  const config = useConfigContext()

  if (!config?.menu) return null

  return (
    <NavigationMenu>
      <NavigationMenuList>
        {config.menu.map((menu) =>
          menu.children && menu.children.length > 0 ? (
            <NavItemWithChildren key={menu.name} menu={menu} />
          ) : (
            <NavItemNoChildren key={menu.name} menu={menu} />
          )
        )}
      </NavigationMenuList>
    </NavigationMenu>
  )
}
