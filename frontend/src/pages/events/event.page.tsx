import { Helmet } from 'react-helmet'
import { Navigate, useParams } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useEventQuery } from '../../api/hooks/useEventQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import CurrentEvent from './components/CurrentEvent'
import { AbsolutePaths } from '../../util/paths'

const EventPage = () => {
  const params = useParams()
  const currentEvent = useEventQuery(params.path!!, () => console.log('Event query failed!'))
  const { sendMessage } = useServiceContext()

  if (currentEvent.isLoading) {
    return <Loading />
  }

  if (currentEvent.isError) {
    sendMessage('Esemény betöltése sikertelen!\n' + currentEvent.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof currentEvent.data === 'undefined') {
    sendMessage('Esemény betöltése sikertelen!\n Keresse az oldal fejlesztőit.')
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
