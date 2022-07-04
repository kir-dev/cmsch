import { Grid, Heading, Text } from '@chakra-ui/react'
import { EventListView } from '../../../util/views/event.view'
import EventListItem from './EventListItem'

interface EventListProps {
  eventList: EventListView[]
}

const EventList = ({ eventList }: EventListProps) => {
  return (
    <>
      <Heading mb={'1rem'}>Események</Heading>
      <Grid templateColumns={{ base: 'repeat(1, auto)', md: 'repeat(2, auto)' }} gap={'4rem'}>
        {eventList.map((e: EventListView) => (
          <EventListItem event={e} key={e.url + e.timestampStart} />
        ))}
      </Grid>
    </>
  )
}

export default EventList
