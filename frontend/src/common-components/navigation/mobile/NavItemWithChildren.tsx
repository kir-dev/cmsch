import { Collapsible, Flex, Icon, Stack, Text, useDisclosure } from '@chakra-ui/react'
import { FaChevronDown } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { Menu } from '../../../api/contexts/config/types'
import LinkComponent from '../LinkComponent'
import { useColorModeValue } from '../../../components/ui/color-mode'

type Props = {
  menu: Menu
}

export const NavItemWithChildren = ({ menu: { external, url, children, name } }: Props) => {
  const { open, onToggle } = useDisclosure()

  return (
    <div style={{ marginTop: '0 !important;' }} onClick={onToggle}>
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
          <Icon as={FaChevronDown} transition="all .25s ease-in-out" transform={open ? 'rotate(180deg)' : ''} w={4} h={4} />
        </Flex>
      </LinkComponent>

      <Collapsible.Root open={open} style={{ marginTop: '0' }}>
        <Collapsible.Content>
          <Stack pl={4} borderLeft={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.800')} align="start">
            {children.map((child) => (
              <Link key={child.url} to={child.url || '#'} className="navitem" style={{ width: '100%' }}>
                <Text key={child.url} py={2}>
                  {child.name}
                </Text>
              </Link>
            ))}
          </Stack>
        </Collapsible.Content>
      </Collapsible.Root>
    </div>
  )
}
