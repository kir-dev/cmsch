import { Grid, useBreakpointValue } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { EventListView } from '../../../util/views/event.view'
import EventListItem from './EventListItem'

interface EventListProps {
  eventList: EventListView[]
}

const EventList = ({ eventList }: EventListProps) => {
  const config = useConfigContext()
  return (
    <Grid templateColumns={`repeat(${useBreakpointValue({ base: 1, md: 2 })}, 1fr)`} gap={3}>
      {eventList.map((e: EventListView) => (
        <EventListItem event={e} key={e.url + e.timestampStart} useLink={config?.components.event.enableDetailedView} />
      ))}
    </Grid>
  )
}

export default EventList
