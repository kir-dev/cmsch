import { Button, Heading, useToast } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { useEffect, useMemo } from 'react'
import { Helmet } from 'react-helmet'
import { Link, useLocation } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { ExampleComponent } from './components/ExampleComponent'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { AbsolutePaths } from '../../util/paths'
import Clock from '../countdown/components/clock'

const IndexPage = () => {
  const location = useLocation()
  const toast = useToast()
  const { onLogout, onLoginSuccess } = useAuthContext()
  const config = useConfigContext()

  const countTo = useMemo(() => {
    const component = config?.components.countdown
    try {
      if (!component) return new Date()
      return new Date(component?.timeToCountTo * 1000)
    } catch (e) {
      return new Date()
    }
  }, [config?.components.countdown])

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
      <Heading size="3xl" textAlign="center" marginTop={10}>
        Üdvözlünk a{' '}
        <Heading as="span" color={useColorModeValue('brand.500', 'brand.600')} size="3xl">
          {config?.components.app.siteName || 'CMSch'}
        </Heading>{' '}
        portálon
      </Heading>
      {config?.components.countdown?.enabled && (
        <>
          <Heading textAlign="center">{config?.components.countdown?.topMessage}</Heading>
          <Clock countTo={countTo} />
        </>
      )}
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
