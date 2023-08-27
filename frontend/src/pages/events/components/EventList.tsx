import { Box, Heading, VStack } from '@chakra-ui/react'
import { isSameDay } from 'date-fns'
import { useMemo } from 'react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { EventListView } from '../../../util/views/event.view'
import EventListItem from './EventListItem'

interface EventListProps {
  eventList: EventListView[]
  groupByDay?: boolean
}

const EventList = ({ eventList, groupByDay }: EventListProps) => {
  const config = useConfigContext()
  const eventGroups = useMemo(() => groupEventsByDay(eventList), [eventList])
  if (!groupByDay) return <EventListGroup eventList={eventList} useLink={config?.components.event?.enableDetailedView} />

  return (
    <VStack>
      {eventGroups.map((group) => (
        <Box key={group.date.getTime()} w="full">
          <Heading mb={2}>{group.date.toLocaleDateString('hu-HU', { month: '2-digit', day: '2-digit' })}</Heading>
          <EventListGroup eventList={group.events} useLink={config?.components.event?.enableDetailedView} />
        </Box>
      ))}
    </VStack>
  )
}

interface EventListGroupProps {
  eventList: EventListView[]
  useLink?: boolean
}

function EventListGroup({ eventList, useLink }: EventListGroupProps) {
  return (
    <VStack spacing={3}>
      {eventList.map((e: EventListView) => (
        <EventListItem event={e} key={e.url + e.timestampStart} useLink={useLink} />
      ))}
    </VStack>
  )
}

type EventGroup = {
  date: Date
  events: EventListView[]
}

function groupEventsByDay(eventList: EventListView[]) {
  const groupedEvents: EventGroup[] = []
  eventList.forEach((event) => {
    const date = new Date(event.timestampStart * 1000)
    const existingGroup = groupedEvents.find((group) => {
      return isSameDay(group.date, date)
    })
    if (existingGroup) {
      existingGroup.events.push(event)
    } else {
      groupedEvents.push({
        date: date,
        events: [event]
      })
    }
  })
  return groupedEvents
}

export default EventList
