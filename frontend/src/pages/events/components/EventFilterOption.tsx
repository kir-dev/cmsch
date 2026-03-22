import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible'
import { useTime } from '@/hooks/useDate.ts'
import { isCurrentEvent, isUpcomingEvent } from '@/util/core-functions.util'
import type { EventListView } from '@/util/views/event.view'
import { useEffect, useState } from 'react'
import { CardListItem } from './CardListItem'
import EventList from './EventList'

type EventFilterOptionProps = {
  name: string
  events: EventListView[]
  forceOpen: boolean
}

export const EventFilterOption = ({ name, events, forceOpen }: EventFilterOptionProps) => {
  const [isOpen, setIsOpen] = useState(false)
  const now = useTime(10000)
  useEffect(() => {
    setIsOpen(forceOpen)
  }, [forceOpen])

  const hasCurrentEvent = events.some((event) => isCurrentEvent(now / 1000, event))
  const hasUpcomingEvent = events.some((event) => isUpcomingEvent(now / 1000, event))

  return (
    <Collapsible open={isOpen} onOpenChange={setIsOpen}>
      <CollapsibleTrigger asChild>
        <div className="my-0 flex flex-col gap-0">
          <CardListItem
            showPulsingDot={hasCurrentEvent || hasUpcomingEvent}
            pulsingDotColor={hasUpcomingEvent ? 'text-warning' : 'text-success'}
            title={name}
            open={isOpen}
            toggle={() => setIsOpen(!isOpen)}
          />
        </div>
      </CollapsibleTrigger>
      <CollapsibleContent>
        <div className="rounded-b-[5px] border-x border-b border-border p-2">
          <EventList eventList={events} />
        </div>
      </CollapsibleContent>
    </Collapsible>
  )
}
