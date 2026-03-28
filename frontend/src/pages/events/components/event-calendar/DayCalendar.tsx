import { Button } from '@/components/ui/button'
import { useDate } from '@/hooks/useDate.ts'
import { formatHu } from '@/util/core-functions.util'
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
  const [scale, setScale] = useState(1)
  const now = useDate()
  const [startDate, setStartDate] = useState(startOfDay(now))

  const eventsForThisDay = useMemo(() => mapEventsForDay(events, startDate), [events, startDate])

  const incrementDay = () => {
    setStartDate((prev) => addDays(prev, 1))
  }

  const decrementDay = () => {
    setStartDate((prev) => addDays(prev, -1))
  }

  const incrementScale = () => {
    setScale((prev) => Math.min(prev + 0.2, 2))
  }

  const decrementScale = () => {
    setScale((prev) => Math.max(prev - 0.2, 0.6))
  }

  return (
    <div className="my-5 w-full block md:hidden">
      <div className="flex justify-between items-center">
        <Button variant="ghost" className="text-primary" size="icon" aria-label="Előző nap" onClick={decrementDay}>
          <ChevronLeft className="h-5 w-5" />
        </Button>
        <h2 className="text-xl font-bold">{formatHu(startDate, 'EEEE, MMMM dd.')}</h2>
        <Button variant="ghost" className="text-primary" size="icon" aria-label="Következő nap" onClick={incrementDay}>
          <ChevronRight className="h-5 w-5" />
        </Button>
      </div>
      <ZoomBar incrementScale={incrementScale} decrementScale={decrementScale} scale={scale} />
      <div className="flex flex-row max-h-[820px] mt-5 overflow-y-auto overflow-x-hidden pt-5 items-start space-x-2">
        <HourColumn h={scale * 800} />
        <div className="rounded-md relative w-full p-2 bg-black/2 dark:bg-white/2" style={{ height: scale * 800 }}>
          <CurrentDateBar minTimestamp={startDate.getTime()} maxTimestamp={endOfDay(startDate).getTime()} />
          {eventsForThisDay.map((event) => (
            <EventBox event={event} key={event.url} />
          ))}
        </div>
      </div>
    </div>
  )
}
