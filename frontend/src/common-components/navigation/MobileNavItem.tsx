import { Collapse, Flex, Icon, Stack, Text, useDisclosure } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FaChevronDown } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { INavItem } from '../../util/configs/nav.config'

type Props = {
  navItem: INavItem
}
export const MobileNavItem = ({ navItem: { label, children, path } }: Props) => {
  const { isOpen, onToggle } = useDisclosure()

  return (
    <Stack spacing={4} onClick={children && onToggle}>
      <Link to={children || !path ? '#' : path} className={children ? undefined : 'navitem'}>
        <Flex
          py={2}
          justify="space-between"
          align="center"
          _hover={{
            textDecoration: 'none'
          }}
        >
          <Text color={useColorModeValue('gray.800', 'gray.200')}>{label}</Text>
          {children && <Icon as={FaChevronDown} transition="all .25s ease-in-out" transform={isOpen ? 'rotate(180deg)' : ''} w={6} h={6} />}
        </Flex>
      </Link>

      <Collapse in={isOpen} animateOpacity style={{ marginTop: '0' }}>
        <Stack pl={4} borderLeft={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.800')} align="start">
          {children &&
            children.map((child) => (
              <Link to={child.path || '#'} className="navitem" style={{ width: '100%' }}>
                <Text key={child.label} py={2}>
                  {child.label}
                </Text>
              </Link>
            ))}
        </Stack>
      </Collapse>
    </Stack>
  )
}
