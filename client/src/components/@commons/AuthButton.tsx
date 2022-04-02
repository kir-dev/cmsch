import React from 'react'
import { Button } from '@chakra-ui/react'
import { useAuthContext } from '../../utils/useAuthContext'

export const AuthButton: React.FC = () => {
  const { isLoggedIn, login } = useAuthContext()
  if (isLoggedIn) return null
  return (
    <Button colorScheme="brand" onClick={login}>
      Bejelentkezés
    </Button>
  )
}
