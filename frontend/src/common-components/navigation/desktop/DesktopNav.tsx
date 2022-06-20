import { Stack } from '@chakra-ui/react'
import { NavItemWithChildren } from './NavItemWithChildren'
import { NavItemNoChildren } from './NavItemNoChildren'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { Menu } from '../../../api/contexts/config/types'

type Props = {
  menu: Menu[]
}

export const DesktopNav = ({ menu }: Props) => (
  <Stack direction="row" spacing={4}>
    {menu.map((menu) =>
      menu.children && menu.children.length > 0 ? (
        <NavItemWithChildren key={menu.name} menu={menu} />
      ) : (
        <NavItemNoChildren key={menu.name} menu={menu} />
      )
    )}
  </Stack>
)
