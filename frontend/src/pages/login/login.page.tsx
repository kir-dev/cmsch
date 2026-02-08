import { Box, Button, Text, VStack } from '@chakra-ui/react'
import { FaGoogle, FaKey, FaSignInAlt } from 'react-icons/fa'
import { Navigate } from 'react-router'

import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { l } from '../../util/language'

const LoginPage = () => {
  const { isLoggedIn } = useAuthContext()
  const config = useConfigContext()
  const app = config?.components?.app
  const component = config?.components?.login
  const brandColor = useBrandColor()

  if (!component) return <ComponentUnavailable />

  if (isLoggedIn) return <Navigate replace to="/" />

  return (
    <CmschPage>
      <title>
        {app?.siteName || 'CMSch'} | {l('login-helmet')}
      </title>
      <VStack spacing={10} mb={10}>
        {component.topMessage ? (
          <Box textAlign="center">
            <Markdown text={component.topMessage} />
          </Box>
        ) : (
          <Box mt={4} />
        )}
        {component.authschPromoted && (
          <>
            <Button
              colorScheme={brandColor}
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
              colorScheme={brandColor}
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
              colorScheme={brandColor}
              onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/keycloak`)}
              leftIcon={<FaKey />}
            >
              {component.keycloakAuthName}
            </Button>
          </>
        )}
        {component.bottomMessage && (
          <Box maxW={440} textAlign="center">
            <Markdown text={component.bottomMessage} />
          </Box>
        )}
      </VStack>
    </CmschPage>
  )
}

export default LoginPage
