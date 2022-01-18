import React from 'react'
import { Button } from '@chakra-ui/react'
import { useAuthContext } from '../../utils/useAuthContext'

export const AuthButton: React.FC = () => {
  const { isLoggedIn, login, logout } = useAuthContext()
  return isLoggedIn ? (
    <Button colorScheme="brand" variant="outline" onClick={logout}>
      Kijelentkezés
    </Button>
  ) : (
    <Button colorScheme="brand" onClick={login}>
      Bejelentkezés
    </Button>
  )
}
