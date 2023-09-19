import { Button, Tooltip } from '@chakra-ui/react'
import { TbShield, TbShieldOff } from 'react-icons/tb'

interface RoleButtonProps {
  onRoleChange: () => void
  isAdmin: boolean
}

export function RoleButton({ onRoleChange, isAdmin }: RoleButtonProps) {
  return (
    <Tooltip label="Jogosultság adása">
      <Button colorScheme="yellow" variant="outline" onClick={onRoleChange}>
        {isAdmin ? <TbShieldOff /> : <TbShield />}
      </Button>
    </Tooltip>
  )
}
