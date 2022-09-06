import { Grid, useBreakpointValue, VStack } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { EventListView } from '../../../util/views/event.view'
import EventListItem from './EventListItem'

interface EventListProps {
  eventList: EventListView[]
}

const EventList = ({ eventList }: EventListProps) => {
  const config = useConfigContext()
  return (
    <VStack spacing={3}>
      {eventList.map((e: EventListView) => (
        <EventListItem event={e} key={e.url + e.timestampStart} useLink={config?.components.event.enableDetailedView} />
      ))}
    </VStack>
  )
}

export default EventList
