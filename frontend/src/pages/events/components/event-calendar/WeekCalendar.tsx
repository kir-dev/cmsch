import { Box, Heading, HStack, IconButton, useColorModeValue } from '@chakra-ui/react'
import { addDays, addWeeks, endOfDay, startOfWeek } from 'date-fns'
import { useMemo, useRef, useState } from 'react'
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa'
import { formatHu } from '../../../../util/core-functions.util'
import { EventListView } from '../../../../util/views/event.view'
import { CurrentDateBar } from './CurrentDateBar'
import { EventBox, EventBoxItem } from './EventBox'
import { HourColumn } from './HourColumn'
import { mapEventsForDay } from './utils'
import { ZoomBar } from './ZoomBar'

interface WeekCalendarProps {
  events: EventListView[]
}

export function WeekCalendar({ events }: WeekCalendarProps) {
  const [scale, setScale] = useState(1)
  const ref = useRef<HTMLDivElement>(null)

  const [startDate, setStartDate] = useState(startOfWeek(new Date(), { weekStartsOn: 1 }))

  const bg = useColorModeValue('#00000005', '#FFFFFF05')

  const days = useMemo(() => {
    const daysTemp: { date: Date; events: EventBoxItem[] }[] = []
    for (let i = 0; i < 7; i++) {
      const date = addDays(startDate, i)
      const eventsForDay: EventBoxItem[] = mapEventsForDay(events, date)
      daysTemp.push({ date, events: eventsForDay })
    }
    return daysTemp
  }, [startDate, events])

  const incrementWeek = () => {
    setStartDate((prev) => addWeeks(prev, 1))
  }

  const decrementWeek = () => {
    setStartDate((prev) => addWeeks(prev, -1))
  }

  const incrementScale = () => {
    setScale((prev) => Math.min(prev + 0.2, 2))
  }

  const decrementScale = () => {
    setScale((prev) => Math.max(prev - 0.2, 0.6))
  }

  return (
    <Box my={5} w="full" display={['none', null, 'block']}>
      <HStack justify="space-between">
        <IconButton aria-label="Előző hét" icon={<FaChevronLeft />} onClick={decrementWeek} />
        <Heading as="h2" size="md">
          {formatHu(startDate, 'MM. dd.')} - {formatHu(days[days.length - 1].date, 'MM. dd.')}
        </Heading>
        <IconButton aria-label="Következő hét" icon={<FaChevronRight />} onClick={incrementWeek} />
      </HStack>
      <ZoomBar incrementScale={incrementScale} decrementScale={decrementScale} scale={scale} />
      <HStack flex={1} maxH={830} overflowY="auto" overflowX="hidden" w="full" mt={5} align="flex-start">
        <HourColumn mt={30} h={scale * 800} />
        <HStack flex={1} spacing={1} mt={5} justifyContent="space-evenly" align="flex-start">
          {days.map((day) => (
            <Box key={day.date.toISOString()} w="full">
              <Heading h={30} textAlign="center" as="h3" size="sm" m={0}>
                {formatHu(day.date, 'EEEE')}
              </Heading>
              <Box borderRadius="md" position="relative" h={scale * 800} bg={bg} p={2}>
                <CurrentDateBar minTimestamp={day.date.getTime()} maxTimestamp={endOfDay(day.date).getTime()} />
                {day.events.map((event) => (
                  <EventBox boxRef={ref} event={event} key={event.url} />
                ))}
              </Box>
            </Box>
          ))}
        </HStack>
      </HStack>
    </Box>
  )
}
