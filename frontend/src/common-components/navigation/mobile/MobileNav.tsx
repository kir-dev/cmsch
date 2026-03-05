import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { NavItemNoChildren } from './NavItemNoChildren'
import { NavItemWithChildren } from './NavItemWithChildren'

export const MobileNav: React.FC = () => {
  const config = useConfigContext()

  return (
    <div className="flex flex-col p-4 md:hidden space-y-2">
      {config?.menu.map((menu) =>
        menu.children && menu.children.length > 0 ? (
          <NavItemWithChildren key={menu.name} menu={menu} />
        ) : (
          <NavItemNoChildren key={menu.name} menu={menu} />
        )
      )}
    </div>
  )
}
