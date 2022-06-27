import { Grid, GridItem, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { EventListView } from '../../../util/views/event.view'

type ScheduleProps = {
  events: EventListView[]
}
export const Schedule = ({ events }: ScheduleProps) => (
  <Grid templateColumns="repeat(2, auto)" gap={10} marginTop={10}>
    {events.map((event, idx) => (
      <EventDisplay key={idx} event={event} />
    ))}
  </Grid>
)

type EventDisplayProps = {
  event: EventListView
}

const EventDisplay = ({ event }: EventDisplayProps) => (
  <>
    <GridItem textAlign="right">
      <Text fontSize="2xl" color={useColorModeValue('brand.500', 'brand.600')}>
        {event.timestampStart}-{event.timestampEnd}
      </Text>
    </GridItem>
    <GridItem>
      <Text fontSize="2xl">{event.title}</Text>
      <Text as="i" fontSize="xl" color="gray.500">
        {event.place}
      </Text>
    </GridItem>
  </>
)
