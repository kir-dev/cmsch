import { Button, ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useNavigate } from 'react-router'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { l } from '../../util/language'

export const UnauthorizedPage = () => {
  const navigate = useNavigate()
  const brandColor = useBrandColor()

  return (
    <CmschPage>
      <Helmet title={l('unauthorized-page-helmet')} />
      <Heading as="h1" variant="main-title" textAlign="center">
        {l('unauthorized-page-title')}
      </Heading>
      <Text textAlign="center" color="gray.500" marginTop={10}>
        {l('unauthorized-page-description')}
      </Text>
      <ButtonGroup justifyContent="center" marginTop={10}>
        <Button colorScheme={brandColor} onClick={() => navigate('/login')}>
          Belépés
        </Button>
        <LinkButton href="/" variant="ghost">
          Főoldal
        </LinkButton>
      </ButtonGroup>
    </CmschPage>
  )
}
