import { Button, Flex, Icon, IconButton } from '@chakra-ui/react'
import { FaChevronDown } from 'react-icons/fa'
import { useNavigate } from 'react-router-dom'
import { Menu } from '../../../api/contexts/config/types'

type Props = {
  menu: Menu
  onToggle: () => void
  isOpen: boolean
  onButtonClickSideEffect: () => void
}

export const HeaderHasUrl = ({ menu: { url, external, name }, onToggle, isOpen, onButtonClickSideEffect }: Props) => {
  const navigate = useNavigate()
  const onClick = () => {
    if (external) window.open(url)
    else navigate(url)
    onButtonClickSideEffect()
  }

  return (
    <Flex w="full" justify="space-between" alignItems="center">
      <Button flex={1} onClick={onClick} variant="ghost" justifyContent="start" p={2}>
        {name}
      </Button>
      <IconButton
        aria-label={`${name} menü lenyitása`}
        variant="ghost"
        p={2}
        ml={4}
        onClick={onToggle}
        icon={<Icon as={FaChevronDown} w={4} h={4} transition="all .25s ease-in-out" transform={isOpen ? 'rotate(180deg)' : ''} />}
      />
    </Flex>
  )
}
