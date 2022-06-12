import { Stack } from '@chakra-ui/react'
import React from 'react'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { NAV_ITEMS } from '../../util/configs/nav.config'
import { MobileNavItem } from './MobileNavItem'

export const MobileNav: React.FC = () => {
  const { isLoggedIn } = useAuthContext()
  return (
    <Stack p={4} display={{ md: 'none' }}>
      {NAV_ITEMS.filter((navItem) => navItem.shouldBeShown(isLoggedIn)).map((navItem) => (
        <MobileNavItem key={navItem.label} navItem={navItem} />
      ))}
    </Stack>
  )
}
