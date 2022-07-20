import { useToast } from '@chakra-ui/react'
import { useEffect } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

const IndexPage = () => {
  const location = useLocation()
  const toast = useToast()
  const { onLogout, onLoginSuccess } = useAuthContext()
  const config = useConfigContext()

  useEffect(() => {
    if (location.pathname === '/logout') {
      onLogout()
    }

    const searchParams = new URLSearchParams(location.search)
    if (searchParams.get('logged-out') == 'true') {
      toast({
        title: 'Kijelentkezés',
        description: 'Sikeres kijelentkeztetés!',
        status: 'success',
        duration: 5000,
        isClosable: true
      })
    }
    if (searchParams.has('jwt')) {
      onLoginSuccess({ jwt: searchParams.get('jwt')!! })
    }
  }, [location])

  return <Navigate to={config?.components.app.defaultComponent || '/home'} />
}

export default IndexPage
