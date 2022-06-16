import { Box, HStack, Icon, Popover, PopoverContent, PopoverTrigger, Stack, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FaChevronDown } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { DesktopSubNav } from './DesktopSubNav'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

export const DesktopNav = () => {
  const config = useConfigContext()

  return (
    <Stack direction="row" spacing={4}>
      {config?.menu &&
        config?.menu.map((menu) => (
          <Box key={menu.name} p={2}>
            <Popover trigger="hover" placement="bottom-start">
              <PopoverTrigger>
                <Link to={menu.url || '#'}>
                  <HStack
                    _hover={{
                      textDecoration: 'none',
                      color: useColorModeValue('brand.500', 'brand.600')
                    }}
                  >
                    <Text fontSize="md" fontWeight={500}>
                      {menu.name}
                    </Text>
                    {menu.children && menu.children.length > 0 && <Icon as={FaChevronDown} w={6} h={6} m={0} />}
                  </HStack>
                </Link>
              </PopoverTrigger>

              {menu.children && menu.children.length > 0 && (
                <PopoverContent border={0} boxShadow="xl" bg={useColorModeValue('gray.50', 'gray.800')} p={4} rounded="xl" maxW="2xs">
                  <Stack>
                    {menu.children.map((child) => (
                      <DesktopSubNav key={child.name} menu={child} />
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
