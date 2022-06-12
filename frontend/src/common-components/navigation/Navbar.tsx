import { Box, Collapse, Flex, IconButton, useColorModeValue, useDisclosure, Image, Icon } from '@chakra-ui/react'
import { DesktopNav } from './DesktopNav'
import { MobileNav } from './MobileNav'
import { Link } from 'react-router-dom'
import { FaBars, FaTimes } from 'react-icons/fa'
import { ColorModeSwitcher } from './ColorModeSwitcher'

export const Navbar = () => {
  const { isOpen, onToggle } = useDisclosure()

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
            icon={isOpen ? <Icon as={FaTimes} w={3} h={3} /> : <Icon as={FaBars} w={5} h={5} />}
            variant="ghost"
            aria-label="Navigáció megnyitása"
          />
        </Flex>
        <Flex flex={{ base: 1 }} justify={{ base: 'center', md: 'start' }}>
          <Link to="/">
            <Image src={`/img/${useColorModeValue('navbar_logo.png', 'navbar_logo_white.png')}`} width="12.938rem" />
          </Link>
        </Flex>
        <Flex display={{ base: 'none', md: 'flex' }} flex={{ base: 1 }} justify={{ base: 'center', md: 'flex-end' }}>
          <Flex display={{ base: 'none', md: 'flex' }} mx={4}>
            <DesktopNav />
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
        <MobileNav />
      </Collapse>
    </Box>
  )
}
