import { Stack } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { NavItemNoChildren } from './NavItemNoChildren'
import { NavItemWithChildren } from './NavItemWithChildren'

export const MobileNav: React.FC = () => {
  const config = useConfigContext()

  return (
    <Stack p={4} display={{ md: 'none' }}>
      {config?.menu.map((menu) =>
        menu.children && menu.children.length > 0 ? (
          <NavItemWithChildren key={menu.name} menu={menu} />
        ) : (
          <NavItemNoChildren key={menu.name} menu={menu} />
        )
      )}
    </Stack>
  )
}
