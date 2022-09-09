import { Box, Button, Heading, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { FaGoogle, FaSignInAlt } from 'react-icons/fa'
import { Navigate } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { l } from '../../util/language'
import Markdown from '../../common-components/Markdown'

const LoginPage = () => {
  const { isLoggedIn } = useAuthContext()
  const config = useConfigContext()
  const component = config?.components.login

  if (isLoggedIn) return <Navigate replace to="/" />

  return (
    <CmschPage>
      <Helmet />
      <VStack>
        <Heading size="lg" textAlign="center" mt={10} mb={2}>
          {l('login-consent')}
        </Heading>
        {component?.authschPromoted && (
          <Button
            colorScheme="brand"
            onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/authsch`)}
            leftIcon={<FaSignInAlt />}
          >
            {component?.onlyBmeProvider ? 'BME Címtár' : 'AuthSCH'}
          </Button>
        )}
        {component?.googleSsoEnabled && (
          <Button
            colorScheme="brand"
            onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/google`)}
            leftIcon={<FaGoogle />}
          >
            Google SSO
          </Button>
        )}
        <Box mt={5}>
          <Markdown text={component?.bottomMessage} />
        </Box>
      </VStack>
    </CmschPage>
  )
}

export default LoginPage
