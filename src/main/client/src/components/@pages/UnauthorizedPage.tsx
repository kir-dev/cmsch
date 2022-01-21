import React from 'react'
import { Page } from '../@layout/Page'
import { ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { AuthButton } from '../@commons/AuthButton'
import { LinkButton } from '../@commons/LinkButton'

export const UnauthorizedPage: React.FC = () => {
  return (
    <Page title="Hiba">
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
}
