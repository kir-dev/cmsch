import { Box, Collapsible, Stack, useDisclosure } from '@chakra-ui/react'
import { useEffect } from 'react'
import { isCurrentEvent, isUpcomingEvent } from '../../../util/core-functions.util'
import { EventListView } from '../../../util/views/event.view'
import { CardListItem } from './CardListItem'
import EventList from './EventList'

type EventFilterOptionProps = {
  name: string
  events: EventListView[]
  forceOpen: boolean
}

export const EventFilterOption = ({ name, events, forceOpen }: EventFilterOptionProps) => {
  const { open, onToggle, onOpen, onClose } = useDisclosure()
  useEffect(() => {
    if (forceOpen) {
      onOpen()
    } else {
      onClose()
    }
  }, [forceOpen])
  const hasCurrentEvent = events.some(isCurrentEvent)
  const hasUpcomingEvent = events.some(isUpcomingEvent)
  return (
    <Stack gap={0} my={0}>
      <CardListItem
        showPulsingDot={hasCurrentEvent || hasUpcomingEvent}
        pulsingDotColor={hasUpcomingEvent ? 'yellow.400' : undefined}
        title={name}
        open={open}
        toggle={onToggle}
      />
      <Collapsible.Root open={open}>
        <Collapsible.Content>
          <Box borderWidth="0px 2px 2px 2px" borderRadius="0 0 5px 5px" borderColor="whiteAlpha.200" padding={2}>
            <EventList eventList={events} />
          </Box>
        </Collapsible.Content>
      </Collapsible.Root>
    </Stack>
  )
}
