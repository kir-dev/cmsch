import { Heading, Wrap } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { useDevelopers } from '../../api/hooks/useDevelopers'
import { OrganizerSection } from './components/OrganizerSection'
import { DeveloperWrapItem } from './components/DeveloperWrapItem'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { l } from '../../util/language'
import { Navigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import * as React from 'react'

const ImpressumPage = () => {
  const config = useConfigContext()
  const developers = useDevelopers()
  const { sendMessage } = useServiceContext()

  const component = config?.components?.impressum

  if (!component) {
    sendMessage(l('component-unavailable'))
    return <Navigate to={AbsolutePaths.ERROR} />
  }

  return (
    <CmschPage>
      <Helmet title={component.title} />
      <Heading>{component.title}</Heading>
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
