import { Stack } from '@chakra-ui/react'
import { NavItemWithChildren } from './NavItemWithChildren'
import { NavItemNoChildren } from './NavItemNoChildren'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'

export const DesktopNav = () => {
  const config = useConfigContext()

  if (!config?.menu) return null

  return (
    <Stack direction="row" spacing={4}>
      {config.menu.map((menu) =>
        menu.children && menu.children.length > 0 ? (
          <NavItemWithChildren key={menu.name} menu={menu} />
        ) : (
          <NavItemNoChildren key={menu.name} menu={menu} />
        )
      )}
    </Stack>
  )
}
