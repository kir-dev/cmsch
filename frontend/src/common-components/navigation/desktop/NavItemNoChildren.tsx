import type { Menu } from '@/api/contexts/config/types'
import { useBrandColor } from '@/util/core-functions.util.ts'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
}

export const NavItemNoChildren = ({ menu }: Props) => {
  const hoverColor = useBrandColor()
  return (
    <div key={menu.name} className="p-2 navitem">
      <LinkComponent url={menu.url || '#'} external={menu.external}>
        <span
          className="whitespace-nowrap text-md font-medium transition-colors cursor-pointer"
          style={{
            // We use style here because brand colors are dynamic from config
            color: 'inherit'
          }}
          onMouseEnter={(e) => (e.currentTarget.style.color = hoverColor)}
          onMouseLeave={(e) => (e.currentTarget.style.color = 'inherit')}
        >
          {menu.name}
        </span>
      </LinkComponent>
    </div>
  )
}
