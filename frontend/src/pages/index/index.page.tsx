import { Button, Heading, useToast } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { useEffect } from 'react'
import { Helmet } from 'react-helmet'
import { Link, useLocation } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { ExampleComponent } from './components/ExampleComponent'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { AbsolutePaths } from '../../util/paths'

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

  return (
    <CmschPage>
      <Helmet />
      <Heading size="3xl" textAlign="center" marginTop={10} color="accent">
        Üdvözlünk a{' '}
        <Heading as="span" color={useColorModeValue('brand.500', 'brand.600')} size="3xl">
          {config?.components.app.siteName || 'CMSch'}
        </Heading>{' '}
        portálon
      </Heading>
      <Link to={AbsolutePaths.TASKS}>Tasks</Link>
      <ExampleComponent />
      <Link to={AbsolutePaths.EVENTS}>
        <Button>Események</Button>
      </Link>
      <Link to={AbsolutePaths.NEWS}>
        <Button>Hírek</Button>
      </Link>
    </CmschPage>
  )
}

export default IndexPage
