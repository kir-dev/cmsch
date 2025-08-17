import { Heading, Wrap } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'

import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useDevelopers } from '../../api/hooks/developers/useDevelopers'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { DeveloperWrapItem } from './components/DeveloperWrapItem'
import { OrganizerSection } from './components/OrganizerSection'

const ImpressumPage = () => {
  const config = useConfigContext()
  const developers = useDevelopers()

  const component = config?.components?.impressum

  if (!component) return <ComponentUnavailable />

  return (
    <CmschPage>
      <Helmet title={component.title} />
      <Heading as="h1" variant="main-title">
        {component.title}
      </Heading>
      <Markdown text={component.topMessage} />
      <Heading as="h2" size="lg" my="5" textAlign="center">
        Fejlesztők
      </Heading>
      <Wrap justify="center">
        {developers.map((dev) => (
          <DeveloperWrapItem key={dev.name} dev={dev} />
        ))}
      </Wrap>
      <Markdown text={component.developersBottomMessage} />
      <OrganizerSection organizers={component.leadOrganizers || []} message={component.leadOrganizersMessage} title="Rendezők" />
      <OrganizerSection
        organizers={component.otherOrganizers || []}
        message={component.otherOrganizersMessage}
        title="Stáb további tagjai"
      />
    </CmschPage>
  )
}

export default ImpressumPage
