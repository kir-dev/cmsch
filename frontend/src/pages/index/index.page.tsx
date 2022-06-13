import { Heading } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { useEffect } from 'react'
import { Helmet } from 'react-helmet'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { ExampleComponent } from './components/ExampleComponent'

export const IndexPage = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const { onLogout, onLoginSuccess } = useAuthContext()

  useEffect(() => {
    if (location.pathname === '/logout') {
      onLogout()
      navigate('/')
    }

    const searchParams = new URLSearchParams(location.search)
    if (searchParams.get('logged-out') == 'true') {
      onLogout()
      navigate('/')
    }
    if (searchParams.has('jwt')) {
      onLoginSuccess({ jwt: searchParams.get('jwt')!! })
    }
  }, [location])

  return (
    <CmschPage>
      <Helmet />
      <Heading size="3xl" textAlign="center" marginTop={10}>
        Üdvözlünk a{' '}
        <Heading as="span" color={useColorModeValue('brand.500', 'brand.600')} size="3xl">
          CMSch
        </Heading>{' '}
        portálon
      </Heading>
      <ExampleComponent />
    </CmschPage>
  )
}
