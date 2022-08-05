import { Box, Collapse, useDisclosure } from '@chakra-ui/react'
import { EventListView } from '../../../util/views/event.view'
import { CardListItem } from './CardListItem'
import EventList from './EventList'

type EventFilterOptionProps = {
  name: string
  events: EventListView[]
  forceOpen: boolean
}

export const EventFilterOption = ({ name, events, forceOpen }: EventFilterOptionProps) => {
  const { isOpen, onToggle } = useDisclosure()
  return (
    <>
      <CardListItem title={name} open={isOpen} toggle={onToggle} />
      <Collapse in={isOpen || forceOpen} animateOpacity>
        <Box borderWidth="1px" borderColor="whiteAlpha.200">
          {(isOpen || forceOpen) && <EventList eventList={events} />}
        </Box>
      </Collapse>
    </>
  )
}
