import { Box, Heading, HStack, IconButton, useColorModeValue } from '@chakra-ui/react'
import { addDays, endOfDay, startOfDay } from 'date-fns'
import { useMemo, useState } from 'react'
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa'
import { formatHu } from '../../../../util/core-functions.util'
import { EventListView } from '../../../../util/views/event.view'
import { CurrentDateBar } from './CurrentDateBar'
import { EventBox } from './EventBox'
import { HourColumn } from './HourColumn'
import { mapEventsForDay } from './utils'
import { ZoomBar } from './ZoomBar'

interface DayCalendarProps {
  events: EventListView[]
}

export function DayCalendar({ events }: DayCalendarProps) {
  const [scale, setScale] = useState(1)
  const [startDate, setStartDate] = useState(startOfDay(new Date()))

  const bg = useColorModeValue('#00000005', '#FFFFFF05')

  const eventsForThisDay = useMemo(() => mapEventsForDay(events, startDate), [events, startDate])

  const incrementDay = () => {
    setStartDate((prev) => addDays(prev, 1))
  }

  const decrementDay = () => {
    setStartDate((prev) => addDays(prev, -1))
  }

  const incrementScale = () => {
    setScale((prev) => prev + 0.2)
  }

  const decrementScale = () => {
    setScale((prev) => prev - 0.2)
  }

  return (
    <Box my={5} w="full" display={['block', null, 'none']}>
      <HStack justify="space-between">
        <IconButton aria-label="Előző nap" icon={<FaChevronLeft />} onClick={decrementDay} />
        <Heading as="h2" size="md">
          {formatHu(startDate, 'EEEE, MMMM dd.')}
        </Heading>
        <IconButton aria-label="Következő nap" icon={<FaChevronRight />} onClick={incrementDay} />
      </HStack>
      <ZoomBar incrementScale={incrementScale} decrementScale={decrementScale} scale={scale} />
      <HStack maxH={820} mt={5} overflowY="auto" overflowX="hidden" pt={5} align="flex-start">
        <HourColumn h={scale * 800} />
        <Box borderRadius="md" position="relative" w="full" h={scale * 800} bg={bg} p={2}>
          <CurrentDateBar minTimestamp={startDate.getTime()} maxTimestamp={endOfDay(startDate).getTime()} />
          {eventsForThisDay.map((event) => (
            <EventBox event={event} key={event.url} />
          ))}
        </Box>
      </HStack>
    </Box>
  )
}
