import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { NavItemNoChildren } from './NavItemNoChildren'
import { NavItemWithChildren } from './NavItemWithChildren'

interface MobileNavProps {
  onNavigate?: () => void
}

export const MobileNav: React.FC<MobileNavProps> = ({ onNavigate }) => {
  const config = useConfigContext()

  return (
    <div className="flex flex-col p-4 space-y-2 items-center">
      {config?.menu.map((menu) =>
        menu.children && menu.children.length > 0 ? (
          <NavItemWithChildren key={menu.name} menu={menu} onNavigate={onNavigate} />
        ) : (
          <NavItemNoChildren key={menu.name} menu={menu} onNavigate={onNavigate} />
        )
      )}
    </div>
  )
}
