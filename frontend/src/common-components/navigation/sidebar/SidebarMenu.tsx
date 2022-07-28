import { Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerFooter, DrawerHeader, DrawerOverlay } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'
import { Menu } from '../../../api/contexts/config/types'
import { SidebarContent } from './helpers/SidebarContent'
import { SidebarFooter } from './helpers/SidebarFooter'
import { SidebarHeading } from './helpers/SidebarHeading'

type Props = {
  isOpen: boolean
  onClose: () => void
  openerButtonRef?: React.RefObject<any>
}

export const SidebarMenu = ({ isOpen, onClose, openerButtonRef }: Props) => {
  const navigate = useNavigate()
  const loginMenu: Menu = {
    name: 'Bejelentkezés',
    url: '/login',
    external: false,
    children: []
  }
  const onLoginClick = () => {
    navigate(loginMenu.url)
    onClose()
  }

  return (
    <Drawer isOpen={isOpen} placement="left" onClose={onClose} finalFocusRef={openerButtonRef}>
      <DrawerOverlay />
      <DrawerContent>
        <DrawerCloseButton />
        <DrawerHeader>
          <SidebarHeading />
        </DrawerHeader>
        <DrawerBody>
          <SidebarContent onClose={onClose} />
        </DrawerBody>
        <DrawerFooter pb={{ base: 6, md: 12 }}>
          <SidebarFooter loginMenu={loginMenu} onLoginClick={onLoginClick} />
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  )
}
