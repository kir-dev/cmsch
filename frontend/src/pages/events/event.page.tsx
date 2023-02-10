import { Helmet } from 'react-helmet-async'
import { Navigate, useParams } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useEventQuery } from '../../api/hooks/useEventQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import CurrentEvent from './components/CurrentEvent'
import { AbsolutePaths } from '../../util/paths'
import { l } from '../../util/language'
import { LoadingPage } from '../loading/loading.page'

const EventPage = () => {
  const params = useParams()
  const currentEvent = useEventQuery(params.path!!, () => console.log('Event query failed!'))
  const { sendMessage } = useServiceContext()

  if (currentEvent.isLoading) {
    return <LoadingPage />
  }

  if (currentEvent.isError) {
    sendMessage(l('event-load-failed') + currentEvent.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof currentEvent.data === 'undefined') {
    sendMessage(l('event-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  return (
    <CmschPage>
      <Helmet title={currentEvent.data.title} />
      <CurrentEvent event={currentEvent.data} />
    </CmschPage>
  )
}

export default EventPage
