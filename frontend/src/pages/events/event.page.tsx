import { Helmet } from 'react-helmet-async'
import { useParams } from 'react-router'

import { useEventQuery } from '../../api/hooks/event/useEventQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import CurrentEvent from './components/CurrentEvent'

const EventPage = () => {
  const params = useParams()
  const { isLoading, isError, data } = useEventQuery(params.path!)

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} />

  return (
    <CmschPage position="relative">
      <Helmet title={data.title} />
      <CurrentEvent event={data} />
    </CmschPage>
  )
}

export default EventPage
