import { Stack } from '@chakra-ui/react'
import React from 'react'
import { MobileNavItem } from './MobileNavItem'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

export const MobileNav: React.FC = () => {
  const config = useConfigContext()

  return (
    <Stack p={4} display={{ md: 'none' }}>
      {config?.menu.map((menuItem) => (
        <MobileNavItem key={menuItem.name} navItem={menuItem} />
      ))}
    </Stack>
  )
}
