import { Button } from '@chakra-ui/react'
import { FC } from 'react'
import { API_BASE_URL } from '../util/configs/environment.config'
import { useAuthContext } from '../api/contexts/auth/useAuthContext'

export const AuthButton: FC = () => {
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
