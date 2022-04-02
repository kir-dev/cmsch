import React from 'react'
import { Button } from '@chakra-ui/react'
import { useAuthContext } from '../../utils/useAuthContext'
import { API_BASE_URL } from 'utils/configurations'

export const AuthButton: React.FC = () => {
  const { isLoggedIn } = useAuthContext()
  const onLoginClicked = () => {
    window.location.replace(`${API_BASE_URL}/control/login`)
  }

  if (isLoggedIn) return null
  return (
    <Button colorScheme="brand" onClick={onLoginClicked}>
      Bejelentkezés
    </Button>
  )
}
