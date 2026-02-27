import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import type { EventListView } from '@/util/views/event.view'
import { isSameDay } from 'date-fns'
import { useMemo } from 'react'
import EventListItem from './EventListItem'

interface EventListProps {
  eventList: EventListView[]
  groupByDay?: boolean
}

const EventList = ({ eventList, groupByDay }: EventListProps) => {
  const config = useConfigContext()
  const eventGroups = useMemo(() => groupEventsByDay(eventList), [eventList])
  if (!groupByDay) return <EventListGroup eventList={eventList} useLink={config?.components?.event?.enableDetailedView} />

  return (
    <div className="flex flex-col gap-5">
      {eventGroups.map((group) => (
        <div key={group.date.getTime()} className="w-full">
          <h2 className="mb-2 text-xl font-bold">{group.date.toLocaleDateString('hu-HU', { month: '2-digit', day: '2-digit' })}</h2>
          <EventListGroup eventList={group.events} useLink={config?.components?.event?.enableDetailedView} />
        </div>
      ))}
    </div>
  )
}

interface EventListGroupProps {
  eventList: EventListView[]
  useLink?: boolean
}

function EventListGroup({ eventList, useLink }: EventListGroupProps) {
  return (
    <div className="flex flex-col gap-3">
      {eventList.map((e: EventListView) => (
        <EventListItem event={e} key={e.url + e.timestampStart} useLink={useLink} />
      ))}
    </div>
  )
}

type EventGroup = {
  date: Date
  events: EventListView[]
}

function groupEventsByDay(eventList: EventListView[]) {
  const groupedEvents: EventGroup[] = []
  eventList.forEach((event) => {
    const date = new Date(event.timestampStart * 1000)
    const existingGroup = groupedEvents.find((group) => {
      return isSameDay(group.date, date)
    })
    if (existingGroup) {
      existingGroup.events.push(event)
    } else {
      groupedEvents.push({
        date: date,
        events: [event]
      })
    }
  })
  return groupedEvents
}

export default EventList
