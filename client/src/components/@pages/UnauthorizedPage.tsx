import { ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { FC } from 'react'
import { Helmet } from 'react-helmet'
import { AuthButton } from '../@commons/AuthButton'
import { LinkButton } from '../@commons/LinkButton'
import { Page } from '../@layout/Page'

export const UnauthorizedPage: FC = () => (
  <Page>
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
  </Page>
)
