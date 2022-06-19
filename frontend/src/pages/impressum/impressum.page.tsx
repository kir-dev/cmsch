import { Heading, Wrap } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { useDevelopers } from './components/developers'
import { OrganizerSection } from './components/OrganizerSection'
import { parseOrganizerArrayJSON } from './util/arrayFunctions'

const ImpressumPage = () => {
  const config = useConfigContext()
  const developers = useDevelopers()
  const impressumConfig = config?.components?.impressum
  const leadOrganizers = parseOrganizerArrayJSON(impressumConfig?.leadOrganizers)
  const otherOrganizers = parseOrganizerArrayJSON(impressumConfig?.otherOrganizers)

  return (
    <CmschPage>
      <Helmet title={impressumConfig?.title} />
      <Heading>{impressumConfig?.title}</Heading>
      <Markdown text={impressumConfig?.topMessage} />
      <Heading as="h2" size="lg" my="5" textAlign="center">
        Fejlesztők
      </Heading>
      <Wrap justify="center">
        {developers.map((dev) => (
          <DeveloperCard key={dev.name} dev={dev} />
        ))}
      </Wrap>
      <Markdown text={impressumConfig?.developersBottomMessage} />
      <OrganizerSection organizers={leadOrganizers} message={impressumConfig?.leadOrganizersMessage} title="Rendezők" />
      <OrganizerSection organizers={otherOrganizers} message={impressumConfig?.otherOrganizersMessage} title="Stáb további tagjai" />
    </CmschPage>
  )
}

export default ImpressumPage
