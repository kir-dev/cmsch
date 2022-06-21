import { Box, Button, Flex, Icon } from '@chakra-ui/react'
import { FaChevronDown } from 'react-icons/fa'
import { Menu } from '../../../api/contexts/config/types'

type Props = {
  menu: Menu
  onToggle: () => void
  isOpen: boolean
}

export const HeaderNoUrl = ({ menu: { name }, onToggle, isOpen }: Props) => {
  return (
    <Button w="full" variant="ghost" justifyContent="start" onClick={onToggle} p={2}>
      <Flex w="full" justify="space-between" alignItems="center">
        <Box>{name}</Box>
        <Icon as={FaChevronDown} transition="all .25s ease-in-out" transform={isOpen ? 'rotate(180deg)' : ''} w={4} h={4} mr={2} />
      </Flex>
    </Button>
  )
}
