import {
  Avatar,
  Box,
  Button,
  Drawer,
  DrawerBody,
  DrawerCloseButton,
  DrawerContent,
  DrawerFooter,
  DrawerHeader,
  DrawerOverlay,
  Heading,
  HStack,
  Icon,
  Image,
  Stack
} from '@chakra-ui/react'
import { FaHandPeace, FaRegHandPeace, FaSignInAlt } from 'react-icons/fa'
import { Link, useNavigate } from 'react-router-dom'
import { useAuthContext } from '../../../api/contexts/auth/useAuthContext'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { Menu } from '../../../api/contexts/config/types'
import { NavItemNoChildren } from './NavItemNoChildren'
import { NavItemWithChildren } from './NavItemWithChildren'

type Props = {
  isOpen: boolean
  onClose: () => void
  openerButtonRef?: React.RefObject<any>
}

export const SidebarMenu = ({ isOpen, onClose, openerButtonRef }: Props) => {
  const config = useConfigContext()
  const navigate = useNavigate()
  const { isLoggedIn, profile } = useAuthContext()
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
          <Link to="/">
            <Image src={config?.components.app.siteLogoUrl} fallback={<span>CMSch</span>} alt="CMSch" maxHeight="1.5rem" />
          </Link>
        </DrawerHeader>

        <DrawerBody>
          <Stack pt={2}>
            {config?.menu.map((menu) =>
              menu.children && menu.children.length > 0 ? (
                <NavItemWithChildren key={menu.name} menu={menu} onButtonClickSideEffect={onClose} />
              ) : (
                <NavItemNoChildren key={menu.name} menu={menu} onButtonClickSideEffect={onClose} />
              )
            )}
          </Stack>
        </DrawerBody>

        <DrawerFooter>
          {isLoggedIn ? (
            <Stack w="full" p={4} alignItems="stretch">
              <HStack justifyContent="center">
                <Icon as={FaRegHandPeace} w={4} h={4} />
                <Box>Üdv újra itt,</Box>
              </HStack>
              <HStack justifyContent="center">
                <Avatar name={profile?.fullName} size="sm" />
                <Heading as="h3" fontSize="xl">
                  {profile?.fullName}!
                </Heading>
              </HStack>
            </Stack>
          ) : (
            <Stack w="full" p={4} alignItems="stretch">
              <Box textAlign="center">Még nem jelentkeztél be!</Box>
              <Button onClick={onLoginClick} variant="outline" size="lg" leftIcon={<FaSignInAlt />}>
                {loginMenu.name}
              </Button>
            </Stack>
          )}
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  )
}
