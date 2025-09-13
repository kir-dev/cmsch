import { Box, chakra, HStack, Icon, Popover, PopoverContent, PopoverTrigger, Stack, useColorModeValue } from '@chakra-ui/react'
import { FaChevronDown } from 'react-icons/fa'
import { Menu } from '../../../api/contexts/config/types'
import { useBrandColor } from '../../../util/core-functions.util.ts'
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
                  color: useBrandColor(500, 400)
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
