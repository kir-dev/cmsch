import { ChevronDownIcon } from '@chakra-ui/icons'
import { Collapse, Flex, Icon, Stack, Text, useDisclosure } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import React from 'react'
import { NavItem } from '../../../types/NavItem'
import { NAV_ITEMS } from '../../../utils/navItems'
import { Link } from 'react-router-dom'
import { AuthButton } from '../../@commons/AuthButton'
import { useAuthContext } from '../../../utils/useAuthContext'

const MobileNavItem: React.FC<NavItem> = ({ label, children, href }) => {
  const { isOpen, onToggle } = useDisclosure()

  return (
    <Stack spacing={4} onClick={children && onToggle}>
      <Link to={children || !href ? '#' : href} className={children ? undefined : 'navitem'}>
        <Flex
          py={2}
          justify="space-between"
          align="center"
          _hover={{
            textDecoration: 'none'
          }}
        >
          <Text color={useColorModeValue('gray.800', 'gray.200')}>{label}</Text>
          {children && (
            <Icon as={ChevronDownIcon} transition="all .25s ease-in-out" transform={isOpen ? 'rotate(180deg)' : ''} w={6} h={6} />
          )}
        </Flex>
      </Link>

      <Collapse in={isOpen} animateOpacity style={{ marginTop: '0' }}>
        <Stack pl={4} borderLeft={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.800')} align="start">
          {children &&
            children.map((child) => (
              <Link to={child.href || '#'} className="navitem">
                <Text key={child.label} py={2}>
                  {child.label}
                </Text>
              </Link>
            ))}
        </Stack>
      </Collapse>
    </Stack>
  )
}

const MobileNav: React.FC = () => {
  const { isLoggedIn } = useAuthContext()
  return (
    <Stack p={4} display={{ md: 'none' }}>
      {NAV_ITEMS.filter((navItem) => (navItem.loginRequired && isLoggedIn) || !navItem.loginRequired).map((navItem) => (
        <MobileNavItem key={navItem.label} label={navItem.label} children={navItem.children} href={navItem.href} />
      ))}
      <AuthButton />
    </Stack>
  )
}

export { MobileNav }
