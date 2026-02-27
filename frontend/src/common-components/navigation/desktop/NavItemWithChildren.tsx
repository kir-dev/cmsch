import type { Menu } from '@/api/contexts/config/types'
import { DropdownMenu, DropdownMenuContent, DropdownMenuTrigger } from '@/components/ui/dropdown-menu'
import { useBrandColor } from '@/util/core-functions.util.ts'
import { ChevronDown } from 'lucide-react'
import LinkComponent from '../LinkComponent'
import { ChildNavItem } from './ChildNavItem'

type Props = {
  menu: Menu
}

export const NavItemWithChildren = ({ menu }: Props) => {
  const hoverColor = useBrandColor()
  return (
    <div key={menu.name} className="p-2 navitem">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <div
            className="flex items-center space-x-1 cursor-pointer transition-colors"
            onMouseEnter={(e) => (e.currentTarget.style.color = hoverColor)}
            onMouseLeave={(e) => (e.currentTarget.style.color = 'inherit')}
          >
            <LinkComponent url={menu.url || '#'} external={menu.external}>
              <span className="text-md font-medium">{menu.name}</span>
            </LinkComponent>
            <ChevronDown className="h-4 w-4" />
          </div>
        </DropdownMenuTrigger>
        <DropdownMenuContent className="p-4 rounded-xl max-w-[16rem]">
          <div className="flex flex-col space-y-2">
            {menu.children.map((child) => (
              <ChildNavItem key={child.name} menu={child} />
            ))}
          </div>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  )
}
