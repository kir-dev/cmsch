import { Box, Collapse, Flex, Heading, Icon, IconButton, Image, useColorModeValue, useDisclosure } from '@chakra-ui/react'
import { DesktopNav } from './desktop/DesktopNav'
import { MobileNav } from './mobile/MobileNav'
import { Link } from 'react-router-dom'
import { FaBars, FaTimes } from 'react-icons/fa'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { Menu } from '../../api/contexts/config/types'

const LoginMenu: Menu = {
  name: 'Belépés',
  url: '/login',
  external: false,
  children: []
}

export const Navbar = () => {
  const { isOpen, onToggle } = useDisclosure()
  const config = useConfigContext()
  const logoUrl = useColorModeValue(config?.components.style?.lightLogoUrl, config?.components.style?.darkLogoUrl)
  const menu = config?.menu || []
  const menuWithLogin = [...menu, LoginMenu]

  return (
    <Box mx="auto" maxW="6xl" w="full" fontFamily="heading">
      <Flex
        color={useColorModeValue('gray.800', 'white')}
        minH={{ base: '3rem', md: '4.5rem' }}
        maxW={['100%', '100%', '56rem', '72rem']}
        py={{ base: 2 }}
        px={{ base: 4 }}
        align="center"
      >
        <Flex flex={{ base: 1, md: '1' }} ml={{ base: -2, md: 0 }} display={{ base: 'flex', md: 'none' }}>
          <IconButton
            onClick={onToggle}
            icon={isOpen ? <Icon as={FaTimes} w={5} h={5} /> : <Icon as={FaBars} w={5} h={5} />}
            variant="ghost"
            aria-label="Navigáció megnyitása"
          />
        </Flex>
        <Flex flex={{ base: 1 }} justify={{ base: 'center', md: 'start' }}>
          <Link to="/">
            {logoUrl ? <Image maxH={16} maxW={16} src={logoUrl} alt="CMSch" /> : <Heading>{config?.components.app.siteName}</Heading>}
          </Link>
        </Flex>
        <Flex display={{ base: 'none', md: 'flex' }} flex={{ base: 1 }} justify={{ base: 'center', md: 'flex-end' }}>
          <Flex display={{ base: 'none', md: 'flex' }} mx={4}>
            <DesktopNav menu={menuWithLogin} />
          </Flex>
        </Flex>
        <Flex flex={{ base: 1, md: 0 }} mr={{ base: -2, md: 0 }} justify="flex-end">
          <ColorModeSwitcher />
        </Flex>
      </Flex>
      {/*The method in onClick hides the menu items when a menu item is clicked. Works for collapsible items too!*/}
      <Collapse
        in={isOpen}
        animateOpacity
        onClick={(evt) => {
          if ((evt.target as Element).closest('.navitem')) onToggle()
        }}
      >
        <MobileNav menu={menuWithLogin} />
      </Collapse>
    </Box>
  )
}
