import { CloseIcon, HamburgerIcon, MoonIcon, SunIcon } from '@chakra-ui/icons'
import { Box, Collapse, Flex, IconButton, useColorMode, useColorModeValue, useDisclosure } from '@chakra-ui/react'
import * as React from 'react'
import { DesktopNav } from './DesktopNav'
import { MobileNav } from './MobileNav'

type NavbarProps = {}

export const Navbar: React.FC<NavbarProps> = ({}) => {
  const { isOpen, onToggle } = useDisclosure()
  const { colorMode, toggleColorMode } = useColorMode()

  return (
    <Box align="center" fontFamily="heading">
      <Flex
        bg={useColorModeValue('white', 'gray.800')}
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
          />
        </Flex>
        <Flex flex={{ base: 1 }} justify={{ base: 'center', md: 'start' }}>
          <Box
            as={'a'}
            href="/"
            display="block"
            rounded="md"
            _hover={{
              textDecoration: 'none',
              color: 'orange.500'
            }}
          >
            <Box textAlign={{ base: 'center', md: 'left' }} mx="2">
              Logo here
            </Box>
          </Box>
        </Flex>
        <Flex display={{ base: 'none', md: 'flex' }} flex={{ base: 1 }} justify={{ base: 'center', md: 'flex-end' }}>
          <Flex display={{ base: 'none', md: 'flex' }} mx={4}>
            <DesktopNav />
          </Flex>
        </Flex>
        <Flex flex={{ base: 1, md: 0 }} mr={{ base: -2, md: 0 }} justify="flex-end">
          <IconButton
            aria-label="Sötét-világos mód váltás"
            icon={colorMode === 'dark' ? <SunIcon w={5} h={5} /> : <MoonIcon w={5} h={5} />}
            onClick={toggleColorMode}
            variant="ghost"
          />
        </Flex>
      </Flex>

      <Collapse in={isOpen} animateOpacity>
        <MobileNav />
      </Collapse>
    </Box>
  )
}
