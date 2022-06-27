import { Helmet } from 'react-helmet'
import { Navigate } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useEventListQuery } from '../../api/hooks/useEventListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import EventList from './components/EventList'

const EventListPage = () => {
  const eventList = useEventListQuery(() => console.log('Event list query failed!'))
  const { sendMessage } = useServiceContext()

  if (eventList.isLoading) {
    return <Loading />
  }

  if (eventList.isError) {
    sendMessage('Események betöltése sikertelen!\n' + eventList.error.message)
    return <Navigate replace to="/error" />
  }

  if (typeof eventList.data === 'undefined') {
    sendMessage('Események betöltése sikertelen!\n Keresd az oldal fejlesztőit.')
    return <Navigate replace to="/error" />
  }

  return (
    <CmschPage>
      <Helmet title="Események" />
      <EventList eventList={eventList.data} />
    </CmschPage>
  )
}

export default EventListPage
