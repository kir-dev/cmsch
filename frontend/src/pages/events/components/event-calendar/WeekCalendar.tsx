import { Box, Heading, HStack, IconButton, Text, useColorModeValue } from '@chakra-ui/react'
import { addDays, addWeeks, startOfWeek } from 'date-fns'
import { useMemo, useRef, useState } from 'react'
import { FaChevronLeft, FaChevronRight, FaMinusCircle, FaPlusCircle } from 'react-icons/fa'
import { formatHu } from '../../../../util/core-functions.util'
import { EventListView } from '../../../../util/views/event.view'
import { HourColumn } from '../HourColumn'
import { EventBox, EventBoxItem } from './EventBox'
import { mapEventsForDay } from './utils'

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
    setScale((prev) => prev + 0.2)
  }

  const decrementScale = () => {
    setScale((prev) => prev - 0.2)
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
      <HStack justify="center">
        <IconButton aria-label="Kicsinyítés" icon={<FaMinusCircle />} onClick={decrementScale} />
        <Text>Nagyítás</Text>
        <IconButton aria-label="Nagyítás" icon={<FaPlusCircle />} onClick={incrementScale} />
      </HStack>
      <HStack
        maxH={800}
        flex={1}
        overflowY="auto"
        overflowX="hidden"
        w="full"
        spacing={1}
        mt={5}
        justifyContent="space-evenly"
        align="flex-start"
      >
        <HourColumn h={scale * 800} />
        {days.map((day) => (
          <Box key={day.date.toISOString()} w="full">
            <Heading h={30} textAlign="center" as="h3" size="sm" m={0}>
              {formatHu(day.date, 'EEEE')}
            </Heading>
            <Box borderRadius="md" position="relative" h={scale * 800} bg={bg} p={2}>
              {day.events.map((event) => (
                <EventBox boxRef={ref} event={event} key={event.url} />
              ))}
            </Box>
          </Box>
        ))}
      </HStack>
    </Box>
  )
}
