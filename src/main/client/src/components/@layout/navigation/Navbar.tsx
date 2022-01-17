import { CloseIcon, HamburgerIcon } from '@chakra-ui/icons'
import { Box, Collapse, Flex, IconButton, useColorModeValue, useDisclosure } from '@chakra-ui/react'
import * as React from 'react'
import { DesktopNav } from './DesktopNav'
import { MobileNav } from './MobileNav'
import { ColorModeSwitcher } from '../../@commons/ColorModeSwitcher'
import { Link } from 'react-router-dom'

type NavbarProps = {}

export const Navbar: React.FC<NavbarProps> = ({}) => {
  const { isOpen, onToggle } = useDisclosure()

  return (
    <Box align="center" fontFamily="heading">
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
            icon={isOpen ? <CloseIcon w={3} h={3} /> : <HamburgerIcon w={5} h={5} />}
            variant="ghost"
            aria-label="Navigáció megnyitása"
            colorScheme="brand"
          />
        </Flex>
        <Flex flex={{ base: 1 }} justify={{ base: 'center', md: 'start' }}>
          <Link to="/" replace>
            <Box
              display="block"
              rounded="md"
              _hover={{
                textDecoration: 'none',
                color: 'brand.500'
              }}
            >
              <Box textAlign={{ base: 'center', md: 'left' }} mx="2">
                🍐
              </Box>
            </Box>
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
