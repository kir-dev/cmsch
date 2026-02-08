import { useParams } from 'react-router'

import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useEventQuery } from '../../api/hooks/event/useEventQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import CurrentEvent from './components/CurrentEvent'

const EventPage = () => {
  const app = useConfigContext()?.components?.app
  const params = useParams()
  const { isLoading, isError, data } = useEventQuery(params.path!)

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} />

  return (
    <CmschPage position="relative">
      <title>
        {app?.siteName || 'CMSch'} | {data?.title}
      </title>
      <CurrentEvent event={data} />
    </CmschPage>
  )
}

export default EventPage
