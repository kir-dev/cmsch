import { Box, Collapse, Stack, useDisclosure } from '@chakra-ui/react'
import { useEffect } from 'react'
import { EventListView } from '../../../util/views/event.view'
import { CardListItem } from './CardListItem'
import EventList from './EventList'

type EventFilterOptionProps = {
  name: string
  events: EventListView[]
  forceOpen: boolean
}

export const EventFilterOption = ({ name, events, forceOpen }: EventFilterOptionProps) => {
  const { isOpen, onToggle, onOpen, onClose } = useDisclosure()
  useEffect(() => {
    if (forceOpen) {
      onOpen()
    } else {
      onClose()
    }
  }, [forceOpen])
  return (
    <Stack spacing={0} my={0}>
      <CardListItem title={name} open={isOpen} toggle={onToggle} />
      <Collapse in={isOpen}>
        <Box borderWidth="0px 2px 2px 2px" borderRadius="0 0 5px 5px" borderColor="whiteAlpha.200" padding={2}>
          <EventList eventList={events} />
        </Box>
      </Collapse>
    </Stack>
  )
}
