import { Flex } from '@chakra-ui/react'
import { chakra, useColorModeValue } from '@chakra-ui/system'
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
        <chakra.span className="navitem" color={useColorModeValue('gray.800', 'gray.200')}>
          {name}
        </chakra.span>
      </Flex>
    </LinkComponent>
  )
}
