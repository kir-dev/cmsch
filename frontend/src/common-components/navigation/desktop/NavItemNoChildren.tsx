import { Box, chakra } from '@chakra-ui/react'
import { Menu } from '../../../api/contexts/config/types'
import LinkComponent from '../LinkComponent'
import { useColorModeValue } from '../../../components/ui/color-mode'

type Props = {
  menu: Menu
}

export const NavItemNoChildren = ({ menu }: Props) => {
  return (
    <Box key={menu.name} p={2}>
      <LinkComponent url={menu.url || '#'} external={menu.external}>
        <chakra.span
          _hover={{
            textDecoration: 'none',
            color: useColorModeValue('brand.500', 'brand.400')
          }}
          whiteSpace="nowrap"
          fontSize="md"
          fontWeight={500}
        >
          {menu.name}
        </chakra.span>
      </LinkComponent>
    </Box>
  )
}
