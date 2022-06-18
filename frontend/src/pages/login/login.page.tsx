import { Button, Heading, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { FaSignInAlt } from 'react-icons/fa'
import { Navigate } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { API_BASE_URL } from '../../util/configs/environment.config'

const LoginPage = () => {
  const { isLoggedIn } = useAuthContext()

  if (isLoggedIn) return <Navigate replace to="/" />

  return (
    <CmschPage>
      <Helmet />
      <VStack>
        <Heading size="lg" textAlign="center" mt={10} mb={2}>
          Jelentkezz be AuthSCH fiókoddal
        </Heading>
        <Button colorScheme="brand" onClick={() => (window.location.href = `${API_BASE_URL}/control/login`)} leftIcon={<FaSignInAlt />}>
          Bejelentkezés
        </Button>
      </VStack>
    </CmschPage>
  )
}

export default LoginPage
