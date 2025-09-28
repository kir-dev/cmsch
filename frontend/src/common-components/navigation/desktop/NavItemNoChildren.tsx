import { Box, chakra } from '@chakra-ui/react'
import { Menu } from '../../../api/contexts/config/types'
import { useBrandColor } from '../../../util/core-functions.util.ts'
import LinkComponent from '../LinkComponent'

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
            color: useBrandColor(500, 400)
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
