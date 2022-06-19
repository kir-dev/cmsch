import { Heading, Wrap } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { CmschPage } from '../../common-components/layout/CmschPage'
import DeveloperCard from './components/developerCard'
import { useDevelopers } from './components/developers'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import Markdown from '../../common-components/Markdown'

const ImpressumPage = () => {
  const config = useConfigContext()
  const developers = useDevelopers()
  const impressumConfig = config?.components.impressum
  return (
    <CmschPage>
      <Helmet title={impressumConfig?.title} />
      <Heading>{impressumConfig?.title}</Heading>
      <Markdown text={impressumConfig?.topMessage} />
      <Heading as="h2" size="lg" my="5" textAlign="center">
        Fejlesztők
      </Heading>
      <Wrap justify="center">
        {developers.map((d) => (
          <DeveloperCard name={d.name} img={d.img} tags={d.tags} />
        ))}
      </Wrap>
      <Markdown text={impressumConfig?.developersBottomMessage} />
      <Heading as="h2">{impressumConfig?.leadOrganizers}</Heading>
      <Markdown text={impressumConfig?.leadOrganizersMessage} />
      <Heading as="h2">{impressumConfig?.otherOrganizers}</Heading>
      <Markdown text={impressumConfig?.otherOrganizersMessage} />
    </CmschPage>
  )
}

export default ImpressumPage
