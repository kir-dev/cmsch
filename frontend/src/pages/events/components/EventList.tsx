import { Box, Grid, Heading, Text, useBreakpointValue } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { EventListView } from '../../../util/views/event.view'
import EventListItem from './EventListItem'

interface EventListProps {
  eventList: EventListView[]
}

const EventList = ({ eventList }: EventListProps) => {
  const config = useConfigContext()
  return (
    <>
      <Box mb={10}>
        <Heading mb={5}>{config?.components.event.title}</Heading>
        <Text>{config?.components.event.topMessage}</Text>
      </Box>
      <Grid templateColumns={`repeat(${useBreakpointValue({ base: 1, md: 2 })}, 1fr)`} gap={3}>
        {eventList.map((e: EventListView) => (
          <EventListItem event={e} key={e.url + e.timestampStart} />
        ))}
      </Grid>
    </>
  )
}

export default EventList
