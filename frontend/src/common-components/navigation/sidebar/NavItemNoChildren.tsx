import { Button } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'
import { Menu } from '../../../api/contexts/config/types'

type Props = {
  menu: Menu
  onButtonClickSideEffect: () => void
}

export const NavItemNoChildren = ({ menu: { external, name, url }, onButtonClickSideEffect }: Props) => {
  const navigate = useNavigate()
  const onClick = () => {
    if (external) window.open(url)
    else navigate(url)
    onButtonClickSideEffect()
  }

  return (
    <Button onClick={onClick} variant="ghost" justifyContent="start" p={2}>
      {name}
    </Button>
  )
}
