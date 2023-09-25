import { Box, HStack, Icon, Popover, PopoverContent, PopoverTrigger, Stack } from '@chakra-ui/react'
import { chakra, useColorModeValue } from '@chakra-ui/system'
import { FaChevronDown } from 'react-icons/fa'
import { Menu } from '../../../api/contexts/config/types'
import LinkComponent from '../LinkComponent'
import { ChildNavItem } from './ChildNavItem'

type Props = {
  menu: Menu
}

export const NavItemWithChildren = ({ menu }: Props) => {
  const bg = useColorModeValue('darkContainerColor.600', 'darkContainerColor.600')
  return (
    <Box key={menu.name} p={2}>
      <Popover trigger="hover" placement="bottom-start">
        <PopoverTrigger>
          <Box>
            <LinkComponent url={menu.url || '#'} external={menu.external}>
              <HStack
                _hover={{
                  textDecoration: 'none',
                  color: useColorModeValue('brand.500', 'brand.400')
                }}
              >
                <chakra.span fontSize="md" fontWeight={500}>
                  {menu.name}
                </chakra.span>
                <Icon as={FaChevronDown} w={4} h={4} m={0} />
              </HStack>
            </LinkComponent>
          </Box>
        </PopoverTrigger>
        <PopoverContent border={0} boxShadow="xl" bg={bg} p={4} rounded="xl" maxW="2xs">
          <Stack>
            {menu.children.map((child) => (
              <ChildNavItem key={child.name} menu={child} />
            ))}
          </Stack>
        </PopoverContent>
      </Popover>
    </Box>
  )
}
