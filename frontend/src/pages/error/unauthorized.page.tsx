import { ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { AuthButton } from '../../common-components/AuthButton'
import { LinkButton } from '../../common-components/LinkButton'
import { CmschPage } from '../../common-components/layout/CmschPage'

export const UnauthorizedPage = () => (
  <CmschPage>
    <Helmet title="Error 403" />
    <Heading textAlign="center">Bejelentkezés szükséges</Heading>
    <Text textAlign="center" color="gray.500" marginTop={10}>
      Az oldal eléréséhez be kell jelentkezned!
    </Text>
    <ButtonGroup justifyContent="center" marginTop={10}>
      <AuthButton />
      <LinkButton href="/" variant="ghost">
        Főoldal
      </LinkButton>
    </ButtonGroup>
  </CmschPage>
)
