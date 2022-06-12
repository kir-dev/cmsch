import { Box, Flex, HStack, Popover, PopoverContent, Icon, PopoverTrigger, Stack, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FaChevronDown, FaChevronRight } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { INavItem, NAV_ITEMS } from '../../util/configs/nav.config'
import { DesktopSubNav } from './DesktopSubNav'

export const DesktopNav = () => {
  const { isLoggedIn } = useAuthContext()

  return (
    <Stack direction="row" spacing={4}>
      {NAV_ITEMS.filter((navItem) => navItem.shouldBeShown(isLoggedIn)).map((navItem) => (
        <Box key={navItem.label} p={2}>
          <Popover trigger="hover" placement="bottom-start">
            <PopoverTrigger>
              <Link to={navItem.path}>
                <HStack
                  _hover={{
                    textDecoration: 'none',
                    color: useColorModeValue('brand.500', 'brand.600')
                  }}
                >
                  <Text fontSize="md" fontWeight={500}>
                    {navItem.label}
                  </Text>
                  {navItem.children && <Icon as={FaChevronDown} w={6} h={6} m={0} />}
                </HStack>
              </Link>
            </PopoverTrigger>

            {navItem.children && (
              <PopoverContent border={0} boxShadow="xl" bg={useColorModeValue('gray.50', 'gray.800')} p={4} rounded="xl" maxW="2xs">
                <Stack>
                  {navItem.children.map((child) => (
                    <DesktopSubNav key={child.label} navItem={child} />
                  ))}
                </Stack>
              </PopoverContent>
            )}
          </Popover>
        </Box>
      ))}
    </Stack>
  )
}
