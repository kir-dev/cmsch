import { Button, ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { useNavigate } from 'react-router-dom'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'

export const UnauthorizedPage = () => {
  const navigate = useNavigate()

  return (
    <CmschPage>
      <Helmet title="Error 403" />
      <Heading textAlign="center">Bejelentkezés szükséges</Heading>
      <Text textAlign="center" color="gray.500" marginTop={10}>
        Az oldal eléréséhez be kell jelentkezned!
      </Text>
      <ButtonGroup justifyContent="center" marginTop={10}>
        <Button colorScheme="brand" onClick={() => navigate('/login')}>
          Belépés
        </Button>
        <LinkButton href="/" variant="ghost">
          Főoldal
        </LinkButton>
      </ButtonGroup>
    </CmschPage>
  )
}
