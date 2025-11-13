import { chakra, Flex } from '@chakra-ui/react'
import { Menu } from '../../../api/contexts/config/types'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
}

export const NavItemNoChildren = ({ menu: { external, name, url } }: Props) => {
  return (
    <LinkComponent url={url} external={external}>
      <Flex
        py={2}
        justify="space-between"
        align="center"
        _hover={{
          textDecoration: 'none'
        }}
      >
        <chakra.span className="navitem">
          {name}
        </chakra.span>
      </Flex>
    </LinkComponent>
  )
}
