import { Button } from '@/components/ui/button'
import { formatHu, useBrandColor, useColorModeValue } from '@/util/core-functions.util'
import type { EventListView } from '@/util/views/event.view'
import { addDays, endOfDay, startOfDay } from 'date-fns'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { useMemo, useState } from 'react'
import { CurrentDateBar } from './CurrentDateBar'
import { EventBox } from './EventBox'
import { HourColumn } from './HourColumn'
import { mapEventsForDay } from './utils'
import { ZoomBar } from './ZoomBar'

interface DayCalendarProps {
  events: EventListView[]
}

export function DayCalendar({ events }: DayCalendarProps) {
  const brandColor = useBrandColor()
  const [scale, setScale] = useState(1)
  const [startDate, setStartDate] = useState(startOfDay(new Date()))

  const bg = useColorModeValue('bg-black/[0.02]', 'dark:bg-white/[0.02]')

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
    <div className="my-5 w-full block md:hidden">
      <div className="flex justify-between items-center">
        <Button variant="ghost" size="icon" aria-label="Előző nap" onClick={decrementDay} style={{ color: brandColor }}>
          <ChevronLeft className="h-5 w-5" />
        </Button>
        <h2 className="text-xl font-bold">{formatHu(startDate, 'EEEE, MMMM dd.')}</h2>
        <Button variant="ghost" size="icon" aria-label="Következő nap" onClick={incrementDay} style={{ color: brandColor }}>
          <ChevronRight className="h-5 w-5" />
        </Button>
      </div>
      <ZoomBar incrementScale={incrementScale} decrementScale={decrementScale} scale={scale} />
      <div className="flex flex-row max-h-[820px] mt-5 overflow-y-auto overflow-x-hidden pt-5 items-start space-x-2">
        <HourColumn h={scale * 800} />
        <div className={`rounded-md relative w-full p-2 ${bg}`} style={{ height: scale * 800 }}>
          <CurrentDateBar minTimestamp={startDate.getTime()} maxTimestamp={endOfDay(startDate).getTime()} />
          {eventsForThisDay.map((event) => (
            <EventBox event={event} key={event.url} />
          ))}
        </div>
      </div>
    </div>
  )
}
