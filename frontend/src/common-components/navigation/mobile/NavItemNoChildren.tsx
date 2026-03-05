import type { Menu } from '@/api/contexts/config/types'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
}

export const NavItemNoChildren = ({ menu: { external, name, url } }: Props) => {
  return (
    <LinkComponent url={url} external={external}>
      <div className="flex py-2 justify-between items-center hover:no-underline cursor-pointer">
        <span className="navitem font-medium">{name}</span>
      </div>
    </LinkComponent>
  )
}
