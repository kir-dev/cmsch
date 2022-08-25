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
      <Heading textAlign="center">{l('unauthorized-page-title')}</Heading>
      <Text textAlign="center" color="gray.500" marginTop={10}>
        {l('unauthorized-page-description')}
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
