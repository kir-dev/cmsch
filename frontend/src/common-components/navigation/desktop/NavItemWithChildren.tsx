import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import type { Menu } from '@/api/contexts/config/types'
import { NavigationMenuContent, NavigationMenuItem, NavigationMenuTrigger } from '@/components/ui/navigation-menu'
import { useColorModeValue } from '@/util/core-functions.util.ts'
import { ChildNavItem } from './ChildNavItem'

type Props = {
  menu: Menu
}

export const NavItemWithChildren = ({ menu }: Props) => {
  const components = useConfigContext()?.components
  const backdropFilter = useColorModeValue(components?.style?.lightNavbarFilter, components?.style?.darkNavbarFilter)
  const background = useColorModeValue(components?.style?.lightNavbarColor, components?.style?.darkNavbarColor)
  const textColor = useColorModeValue(components?.style?.lightTextColor, components?.style?.darkTextColor)

  return (
    <NavigationMenuItem>
      <NavigationMenuTrigger className="text-md font-medium bg-transparent">{menu.name}</NavigationMenuTrigger>
      <NavigationMenuContent style={{ backdropFilter, backgroundColor: background, color: textColor }}>
        <div className="flex flex-col space-y-1 p-4 min-w-48">
          {menu.children.map((child) => (
            <ChildNavItem key={child.name} menu={child} />
          ))}
        </div>
      </NavigationMenuContent>
    </NavigationMenuItem>
  )
}
