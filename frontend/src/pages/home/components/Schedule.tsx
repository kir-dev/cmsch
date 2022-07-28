import { Grid, GridItem, LinkBox, LinkOverlay, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { Link } from 'react-router-dom'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { AbsolutePaths } from '../../../util/paths'
import { EventListView } from '../../../util/views/event.view'

type ScheduleProps = {
  events: EventListView[]
  verbose?: boolean
}

export const Schedule = ({ events, verbose }: ScheduleProps) => {
  const config = useConfigContext()
  return (
    <>
      {events.map((event, idx) => (
        <EventDisplay verbose={verbose} key={idx} event={event} useLink={config?.components.event.enableDetailedView} />
      ))}
    </>
  )
}

type EventDisplayProps = {
  event: EventListView
  verbose?: boolean
  useLink?: boolean
}

const EventDisplay = ({ event, verbose, useLink }: EventDisplayProps) => (
  <Grid templateColumns="repeat(2, auto)" gap={10} marginTop={10} as={LinkBox}>
    <GridItem textAlign="right">
      <Text fontSize="2xl" color={useColorModeValue('brand.500', 'brand.600')}>
        {verbose ? parseDate(event.timestampStart) : parseTime(event.timestampStart)}-{parseTime(event.timestampEnd)}
      </Text>
    </GridItem>
    <GridItem>
      <Text fontSize="2xl">
        {useLink ? (
          <LinkOverlay as={Link} to={`${AbsolutePaths.EVENTS}/${event.url}`}>
            {event.title}
          </LinkOverlay>
        ) : (
          event.title
        )}
      </Text>
      <Text as="i" fontSize="xl" color="gray.500">
        {event.place}
      </Text>
    </GridItem>
  </Grid>
)

const parseTime = (time: number) => new Date(time).toLocaleTimeString('hu-HU', { hour: '2-digit', minute: '2-digit' })
const parseDate = (time: number) =>
  new Date(time).toLocaleString('hu-HU', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
