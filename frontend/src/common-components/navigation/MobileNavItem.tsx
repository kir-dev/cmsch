import { Collapse, Flex, Icon, Stack, Text, useDisclosure } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FaChevronDown } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { Menu } from '../../api/contexts/config/types'
import LinkComponent from './LinkComponent'

type Props = {
  navItem: { name: string; url: string; external: boolean; children: Menu[] | undefined }
}
export const MobileNavItem = ({ navItem: { name, children, url, external } }: Props) => {
  const { isOpen, onToggle } = useDisclosure()
  if (children && children.length === 0) children = undefined

  return (
    <Stack spacing={4} onClick={children && onToggle}>
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
          {children && <Icon as={FaChevronDown} transition="all .25s ease-in-out" transform={isOpen ? 'rotate(180deg)' : ''} w={6} h={6} />}
        </Flex>
      </LinkComponent>

      <Collapse in={isOpen} animateOpacity style={{ marginTop: '0' }}>
        <Stack pl={4} borderLeft={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.800')} align="start">
          {children &&
            children.map((child) => (
              <Link key={child.url} to={child.url || '#'} className="navitem" style={{ width: '100%' }}>
                <Text key={child.url} py={2}>
                  {child.name}
                </Text>
              </Link>
            ))}
        </Stack>
      </Collapse>
    </Stack>
  )
}
