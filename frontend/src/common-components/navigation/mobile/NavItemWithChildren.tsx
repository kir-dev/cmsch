import { Collapse, Flex, Icon, Stack, Text, useDisclosure } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import styled from '@emotion/styled'
import { FaChevronDown } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { Menu } from '../../../api/contexts/config/types'
import LinkComponent from '../LinkComponent'

type Props = {
  menu: Menu
}

/** Small hack needed because the Chakra system is too strong to accept my overrides */
const MyDiv = styled.div`
  margin-top: 0 !important;
`

export const NavItemWithChildren = ({ menu: { external, url, children, name } }: Props) => {
  const { isOpen, onToggle } = useDisclosure()

  return (
    <MyDiv onClick={onToggle}>
      <LinkComponent url={url} external={external}>
        <Flex
          py={2}
          justify="space-between"
          align="center"
          _hover={{
            textDecoration: 'none'
          }}
        >
          <Text color={useColorModeValue('gray.800', 'gray.200')}>{name}</Text>
          <Icon as={FaChevronDown} transition="all .25s ease-in-out" transform={isOpen ? 'rotate(180deg)' : ''} w={4} h={4} />
        </Flex>
      </LinkComponent>

      <Collapse in={isOpen} animateOpacity style={{ marginTop: '0' }}>
        <Stack pl={4} borderLeft={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.800')} align="start">
          {children.map((child) => (
            <Link key={child.url} to={child.url || '#'} className="navitem" style={{ width: '100%' }}>
              <Text key={child.url} py={2}>
                {child.name}
              </Text>
            </Link>
          ))}
        </Stack>
      </Collapse>
    </MyDiv>
  )
}
