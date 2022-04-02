import { ChevronDownIcon, ChevronRightIcon, Icon } from '@chakra-ui/icons'
import { Box, Flex, HStack, Popover, PopoverContent, PopoverTrigger, Stack, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FC } from 'react'
import { Link } from 'react-router-dom'
import { NavItem } from '../../../types/NavItem'
import { NAV_ITEMS } from '../../../utils/navItems'
import { useAuthContext } from '../../../utils/useAuthContext'
import { AuthButton } from '../../@commons/AuthButton'

const DesktopSubNav: FC<NavItem> = ({ label, href }) => (
  <Link to={href || '#'}>
    <Box role="group" display="block" p={2} rounded="md">
      <Stack direction="row" align="center">
        <Box>
          <Text transition="all .3s ease" _groupHover={{ color: useColorModeValue('brand.500', 'brand.600') }} fontWeight={500}>
            {label}
          </Text>
        </Box>
        <Flex
          transition="all .3s ease"
          transform="translateX(-10px)"
          opacity={0}
          _groupHover={{ opacity: '100%', transform: 'translateX(0)' }}
          justify="flex-end"
          align="center"
          flex={1}
        >
          <Icon color={useColorModeValue('brand.500', 'brand.600')} w={5} h={5} as={ChevronRightIcon} />
        </Flex>
      </Stack>
    </Box>
  </Link>
)

const DesktopNav: FC = () => {
  const { isLoggedIn } = useAuthContext()
  return (
    <Stack direction="row" spacing={4}>
      {NAV_ITEMS.filter((navItem) => (navItem.loginRequired && isLoggedIn) || !navItem.loginRequired).map((navItem) => (
        <Box key={navItem.label} p={2}>
          <Popover trigger="hover" placement="bottom-start">
            <PopoverTrigger>
              <Link to={navItem.href || '#'} style={navItem.href ? {} : { cursor: 'default' }}>
                <HStack
                  _hover={{
                    textDecoration: 'none',
                    color: useColorModeValue('brand.500', 'brand.600')
                  }}
                >
                  <Text fontSize="md" fontWeight={500}>
                    {navItem.label}
                  </Text>
                  {navItem.children && <Icon as={ChevronDownIcon} w={6} h={6} m={0} />}
                </HStack>
              </Link>
            </PopoverTrigger>

            {navItem.children && (
              <PopoverContent border={0} boxShadow="xl" bg={useColorModeValue('gray.50', 'gray.800')} p={4} rounded="xl" maxW="2xs">
                <Stack>
                  {navItem.children.map((child) => (
                    <DesktopSubNav key={child.label} label={child.label} href={child.href} />
                  ))}
                </Stack>
              </PopoverContent>
            )}
          </Popover>
        </Box>
      ))}
      <AuthButton />
    </Stack>
  )
}

export { DesktopNav }
