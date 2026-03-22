import type { Menu } from '@/api/contexts/config/types'
import { NavigationMenuItem, NavigationMenuLink } from '@/components/ui/navigation-menu'
import LinkComponent from '../LinkComponent'

export const NavItemNoChildren = ({ menu }: { menu: Menu }) => {
  return (
    <NavigationMenuItem>
      <NavigationMenuLink asChild>
        <LinkComponent url={menu.url || '#'} external={menu.external}>
          <span className="whitespace-nowrap text-md font-medium transition-colors cursor-pointer hover:text-primary px-2 py-2">
            {menu.name}
          </span>
        </LinkComponent>
      </NavigationMenuLink>
    </NavigationMenuItem>
  )
}
