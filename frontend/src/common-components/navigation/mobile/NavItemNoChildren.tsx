import type { Menu } from '@/api/contexts/config/types'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
  onNavigate?: () => void
}

export const NavItemNoChildren = ({ menu: { external, name, url }, onNavigate }: Props) => {
  return (
    <LinkComponent url={url} external={external}>
      <div className="flex py-2 justify-between items-center hover:no-underline cursor-pointer" onClick={onNavigate}>
        <span className="font-medium">{name}</span>
      </div>
    </LinkComponent>
  )
}
