import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useDevelopers } from '@/api/hooks/developers/useDevelopers'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { DeveloperWrapItem } from './components/DeveloperWrapItem'
import { OrganizerSection } from './components/OrganizerSection'

const ImpressumPage = () => {
  const developers = useDevelopers()
  const component = useConfigContext()?.components?.impressum

  if (!component) return <ComponentUnavailable />

  return (
    <CmschPage title={component?.title}>
      <h1 className="text-3xl font-bold font-heading">{component.title}</h1>
      <div className="mt-5">
        <Markdown text={component.topMessage} />
      </div>
      <h2 className="text-2xl font-bold my-5 text-center">Fejlesztők</h2>
      <div className="flex flex-wrap justify-center gap-4">
        {developers.map((dev) => (
          <DeveloperWrapItem key={dev.name} dev={dev} />
        ))}
      </div>
      <div className="mt-5">
        <Markdown text={component.developersBottomMessage} />
      </div>
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
