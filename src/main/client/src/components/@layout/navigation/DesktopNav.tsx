import { ChevronRightIcon, Icon } from '@chakra-ui/icons'
import { Box, Flex, Popover, PopoverContent, PopoverTrigger, Stack, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import React from 'react'
import { NavItem } from '../../../types/NavItem'
import { NAV_ITEMS } from '../../../utils/navItems'

const DesktopSubNav: React.FC<NavItem> = ({ label, href }) => {
  return (
    <Box as={'a'} href={href} role="group" display="block" p={2} rounded="md" _hover={{ bg: useColorModeValue('orange.50', 'gray.900') }}>
      <Stack direction="row" align="center">
        <Box>
          <Text transition="all .3s ease" _groupHover={{ color: 'orange.500' }} fontWeight={500}>
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
          <Icon color="orange.500" w={5} h={5} as={ChevronRightIcon} />
        </Flex>
      </Stack>
    </Box>
  )
}

const DesktopNav: React.FC = () => {
  return (
    <Stack direction="row" spacing={4}>
      {NAV_ITEMS.map((navItem) => (
        <Box key={navItem.label}>
          <Popover trigger="hover" placement="bottom-start">
            <PopoverTrigger>
              <Text
                p={2}
                as={'a'}
                href={navItem.href ?? '#'}
                fontSize="md"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: 'orange.500'
                }}
              >
                {navItem.label}
              </Text>
            </PopoverTrigger>

            {navItem.children && (
              <PopoverContent border={0} boxShadow="xl" bg={useColorModeValue('white', 'gray.800')} p={4} rounded="xl" maxW="2xs">
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
    </Stack>
  )
}

export { DesktopNav }
