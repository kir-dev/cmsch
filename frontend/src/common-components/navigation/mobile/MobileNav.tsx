import { Stack } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { Menu } from '../../../api/contexts/config/types'
import { NavItemNoChildren } from './NavItemNoChildren'
import { NavItemWithChildren } from './NavItemWithChildren'

type Props = {
  menu: Menu[]
}

export const MobileNav = ({ menu }: Props) => (
  <Stack p={4} display={{ md: 'none' }}>
    {menu.map((menu) =>
      menu.children && menu.children.length > 0 ? (
        <NavItemWithChildren key={menu.name} menu={menu} />
      ) : (
        <NavItemNoChildren key={menu.name} menu={menu} />
      )
    )}
  </Stack>
)
