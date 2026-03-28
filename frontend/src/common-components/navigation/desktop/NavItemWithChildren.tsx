import type { Menu } from '@/api/contexts/config/types'
import { NavigationMenuContent, NavigationMenuItem, NavigationMenuTrigger } from '@/components/ui/navigation-menu'
import { ChildNavItem } from './ChildNavItem'

type Props = {
  menu: Menu
}

export const NavItemWithChildren = ({ menu }: Props) => {
  return (
    <NavigationMenuItem>
      <NavigationMenuTrigger className="text-md font-medium bg-transparent">{menu.name}</NavigationMenuTrigger>
      <NavigationMenuContent>
        <div className="flex flex-col space-y-1 p-4 w-[16rem]">
          {menu.children.map((child) => (
            <ChildNavItem key={child.name} menu={child} />
          ))}
        </div>
      </NavigationMenuContent>
    </NavigationMenuItem>
  )
}
