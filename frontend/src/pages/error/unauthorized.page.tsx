import { Button, ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useNavigate } from 'react-router-dom'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { l } from '../../util/language'

export const UnauthorizedPage = () => {
  const navigate = useNavigate()

  return (
    <CmschPage>
      <Helmet title={l('unauthorized-page-helmet')} />
      <Heading as="h1" variant="main-title" textAlign="center">
        {l('unauthorized-page-title')}
      </Heading>
      <Text textAlign="center" color="text.500" marginTop={10}>
        {l('unauthorized-page-description')}
      </Text>
      <ButtonGroup justifyContent="center" marginTop={10}>
        <Button colorScheme="brand" color="brandForeground" onClick={() => navigate('/login')}>
          Belépés
        </Button>
        <LinkButton href="/" variant="ghost">
          Főoldal
        </LinkButton>
      </ButtonGroup>
    </CmschPage>
  )
}
