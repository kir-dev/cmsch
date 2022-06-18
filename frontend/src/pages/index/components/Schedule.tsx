import { Grid, GridItem, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { EventView } from '../../../util/views/event.view'

type ScheduleProps = {
  events: EventView[]
}
export const Schedule = ({ events }: ScheduleProps) => (
  <Grid templateColumns="repeat(2, auto)" gap={10} marginTop={10}>
    {events.map((event, idx) => (
      <EventDisplay key={idx} event={event} />
    ))}
  </Grid>
)

type EventDisplayProps = {
  event: EventView
}

const EventDisplay = ({ event }: EventDisplayProps) => (
  <>
    <GridItem textAlign="right">
      <Text fontSize="2xl" color={useColorModeValue('brand.500', 'brand.600')}>
        {event.start}-{event.end}
      </Text>
    </GridItem>
    <GridItem>
      <Text fontSize="2xl">{event.name}</Text>
      <Text as="i" fontSize="xl" color="gray.500">
        {event.location}
      </Text>
    </GridItem>
  </>
)
