import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { AbsolutePaths } from '@/util/paths'
import type { EventListView } from '@/util/views/event.view'
import { Link } from 'react-router'

type ScheduleProps = {
  events: EventListView[]
  verbose?: boolean
}

export const Schedule = ({ events, verbose }: ScheduleProps) => {
  const config = useConfigContext()
  return (
    <div className="w-full">
      {events.map((event, idx) => (
        <EventDisplay verbose={verbose} key={idx} event={event} useLink={config?.components?.event?.enableDetailedView} />
      ))}
    </div>
  )
}

type EventDisplayProps = {
  event: EventListView
  verbose?: boolean
  useLink?: boolean
}

const EventDisplay = ({ event, verbose, useLink }: EventDisplayProps) => {
  return (
    <div className="grid grid-cols-[auto_1fr] gap-10 mt-10 relative group">
      <div className="text-right">
        <span className="text-2xl font-bold text-primary">
          {verbose ? parseDate(event.timestampStart) : parseTime(event.timestampStart)}-{parseTime(event.timestampEnd)}
        </span>
      </div>
      <div>
        <div className="text-2xl">
          {useLink ? (
            <Link to={`${AbsolutePaths.EVENTS}/${event.url}`} className="hover:underline after:absolute after:inset-0">
              {event.title}
            </Link>
          ) : (
            event.title
          )}
        </div>
        <p className="italic text-xl opacity-70">{event.place}</p>
      </div>
    </div>
  )
}

const parseTime = (time: number) => new Date(time).toLocaleTimeString('hu-HU', { hour: '2-digit', minute: '2-digit' })
const parseDate = (time: number) =>
  new Date(time).toLocaleString('hu-HU', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
