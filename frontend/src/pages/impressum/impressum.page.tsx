import { Heading, Wrap } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { useDevelopers } from '../../api/hooks/useDevelopers'
import { DeveloperWrapItem } from './components/DeveloperWrapItem'

const ImpressumPage = () => {
  const config = useConfigContext()
  const developers = useDevelopers()
  const impressumConfig = config?.components?.impressum

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
          <DeveloperWrapItem key={dev.name} dev={dev} />
        ))}
      </Wrap>
      <Markdown text={impressumConfig?.developersBottomMessage} />
    </CmschPage>
  )
}

export default ImpressumPage
