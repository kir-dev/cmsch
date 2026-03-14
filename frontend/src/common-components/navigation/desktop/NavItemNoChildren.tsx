import type { Menu } from '@/api/contexts/config/types'
import LinkComponent from '../LinkComponent'

export const NavItemNoChildren = ({ menu }: { menu: Menu }) => {
  return (
    <div key={menu.name} className="p-2 navitem">
      <LinkComponent url={menu.url || '#'} external={menu.external}>
        <span className="whitespace-nowrap text-md font-medium transition-colors cursor-pointer hover:text-primary">{menu.name}</span>
      </LinkComponent>
    </div>
  )
}
