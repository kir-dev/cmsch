import { Button } from '@/components/ui/button'
import { formatHu, useBrandColor, useColorModeValue } from '@/util/core-functions.util'
import type { EventListView } from '@/util/views/event.view'
import { addDays, addWeeks, endOfDay, startOfWeek } from 'date-fns'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { useMemo, useRef, useState } from 'react'
import { CurrentDateBar } from './CurrentDateBar'
import { EventBox, type EventBoxItem } from './EventBox'
import { HourColumn } from './HourColumn'
import { mapEventsForDay } from './utils'
import { ZoomBar } from './ZoomBar'

interface WeekCalendarProps {
  events: EventListView[]
}

export function WeekCalendar({ events }: WeekCalendarProps) {
  const brandColor = useBrandColor()
  const [scale, setScale] = useState(1)
  const ref = useRef<HTMLDivElement>(null)

  const [startDate, setStartDate] = useState(startOfWeek(new Date(), { weekStartsOn: 1 }))

  const bg = useColorModeValue('bg-black/[0.02]', 'dark:bg-white/[0.02]')

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
    <div className="my-5 w-full hidden md:block">
      <div className="flex justify-between items-center">
        <Button variant="ghost" size="icon" aria-label="Előző hét" onClick={decrementWeek} style={{ color: brandColor }}>
          <ChevronLeft className="h-5 w-5" />
        </Button>
        <h2 className="text-xl font-bold">
          {formatHu(startDate, 'MM. dd.')} - {formatHu(days[days.length - 1].date, 'MM. dd.')}
        </h2>
        <Button variant="ghost" size="icon" aria-label="Következő hét" onClick={incrementWeek} style={{ color: brandColor }}>
          <ChevronRight className="h-5 w-5" />
        </Button>
      </div>
      <ZoomBar incrementScale={incrementScale} decrementScale={decrementScale} scale={scale} />
      <div className="flex flex-row max-h-[830px] overflow-y-auto overflow-x-hidden w-full mt-5 items-start">
        <div className="mt-[30px]">
          <HourColumn h={scale * 800} />
        </div>
        <div className="flex flex-1 mt-5 justify-evenly items-start space-x-1">
          {days.map((day) => (
            <div key={day.date.toISOString()} className="w-full">
              <h3 className="h-[30px] text-center text-sm font-bold m-0">{formatHu(day.date, 'EEEE')}</h3>
              <div className={`rounded-md relative p-2 ${bg}`} style={{ height: scale * 800 }}>
                <CurrentDateBar minTimestamp={day.date.getTime()} maxTimestamp={endOfDay(day.date).getTime()} />
                {day.events.map((event) => (
                  <EventBox boxRef={ref} event={event} key={event.url} />
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
