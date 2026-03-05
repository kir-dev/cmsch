import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { EventIndicator } from '@/common-components/EventIndicator'
import { cn } from '@/lib/utils'
import { isCurrentEvent, isUpcomingEvent, stringifyTimeRange } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { EventListView } from '@/util/views/event.view'
import { Link, Navigate } from 'react-router'
import EventTags from './EventTags'

interface EventListItemProps {
  event: EventListView
  useLink?: boolean
}

const EventListItem = ({ event, useLink }: EventListItemProps) => {
  const config = useConfigContext()

  if (typeof config === 'undefined') {
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  const innerComponent = (
    <div className="relative w-full overflow-hidden rounded-md border border-border">
      <div className="bg-cover bg-center" style={{ backgroundImage: event.previewImageUrl ? `url(${event.previewImageUrl})` : undefined }}>
        <div className={cn('p-4', event.previewImageUrl ? 'bg-background/70' : '')}>
          <h3 className="my-0 text-2xl font-bold">
            {useLink ? (
              <Link to={`${AbsolutePaths.EVENTS}/${event.url}`} className="hover:underline">
                {event.title}
              </Link>
            ) : (
              event.title
            )}
          </h3>
          <p className="text-muted-foreground">{stringifyTimeRange(event.timestampStart * 1000, event.timestampEnd * 1000)}</p>
        </div>
      </div>
      <div className="p-4 bg-card text-card-foreground">
        <p className="mb-4">{event.previewDescription}</p>
        <EventTags tags={[event.category, event.place]} />
      </div>
      <EventIndicator className="absolute right-4 top-4" isCurrent={isCurrentEvent(event)} isUpcoming={isUpcomingEvent(event)} />
    </div>
  )

  return (
    <div className="w-full">
      {config.components?.event?.enableDetailedView ? (
        <Link to={`${AbsolutePaths.EVENTS}/${event.url}`}> {innerComponent} </Link>
      ) : (
        innerComponent
      )}
    </div>
  )
}

export default EventListItem
