import { Button, Heading, Text, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { FaGoogle, FaKey, FaSignInAlt } from 'react-icons/fa'
import { Navigate } from 'react-router-dom'

import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { l } from '../../util/language'

const LoginPage = () => {
  const { isLoggedIn } = useAuthContext()
  const config = useConfigContext()
  const component = config?.components.login

  if (!component) return <ComponentUnavailable />

  if (isLoggedIn) return <Navigate replace to="/" />

  return (
    <CmschPage>
      <Helmet title={l('login-helmet')} />
      <VStack spacing={10} mb={10}>
        <Heading size="lg" textAlign="center" mt={10} mb={2}>
          {l('login-consent')}
        </Heading>
        {component.authschPromoted && (
          <>
            <Button
              colorScheme="brand"
              onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/authsch`)}
              leftIcon={<FaSignInAlt />}
            >
              {component.onlyBmeProvider ? 'BME Címtár' : 'AuthSCH'}
            </Button>
            {(component.googleSsoEnabled || component.keycloakEnabled) && <Text>vagy</Text>}
          </>
        )}
        {component.googleSsoEnabled && (
          <>
            <Button
              colorScheme="brand"
              onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/google`)}
              leftIcon={<FaGoogle />}
            >
              Google SSO
            </Button>
            {component.keycloakEnabled && <Text>vagy</Text>}
          </>
        )}
        {component.keycloakEnabled && (
          <>
            <Button
              colorScheme="brand"
              onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/keycloak`)}
              leftIcon={<FaKey />}
            >
              {component.keycloakAuthName}
            </Button>
          </>
        )}
        {component.bottomMessage && <Markdown text={component.bottomMessage} />}
      </VStack>
    </CmschPage>
  )
}

export default LoginPage
