import { CustomBreadcrumb } from '@/common-components/CustomBreadcrumb'
import { EventIndicator } from '@/common-components/EventIndicator'
import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { DETAILED_TIMESTAMP_OPTIONS, isCurrentEvent, isUpcomingEvent, stringifyTimeStamp } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { EventView } from '@/util/views/event.view'
import EventTags from './EventTags'

interface EventProps {
  event: EventView
}

const CurrentEvent = ({ event }: EventProps) => {
  const breadcrumbItems = [
    {
      title: 'Események',
      to: AbsolutePaths.EVENTS
    },
    {
      title: event.title
    }
  ]
  return (
    <div className="relative">
      <EventIndicator showLabel className="absolute top-5 right-5" isCurrent={isCurrentEvent(event)} isUpcoming={isUpcomingEvent(event)} />
      <CustomBreadcrumb items={breadcrumbItems} />
      <h2 className="text-3xl font-bold font-heading mt-2">{event.title}</h2>
      <p>
        {stringifyTimeStamp(event.timestampStart, DETAILED_TIMESTAMP_OPTIONS)} &mdash;{' '}
        {stringifyTimeStamp(event.timestampEnd, DETAILED_TIMESTAMP_OPTIONS)}
      </p>
      <div className="my-1">
        <EventTags tags={[event.category, event.place]} />
      </div>
      {event.fullImageUrl && event.fullImageUrl !== '' && (
        <img className="mb-4 block mx-auto max-h-80 object-contain" src={event.fullImageUrl} alt={event.title} />
      )}
      <div className="mt-4">
        <Markdown text={event.description} />
      </div>
      <div className="flex justify-between mt-10">
        <LinkButton href={AbsolutePaths.EVENTS} variant="outline">
          Vissza
        </LinkButton>
        {event.extraButtonUrl && (
          <LinkButton href={event.extraButtonUrl} external>
            {event.extraButtonTitle}
          </LinkButton>
        )}
      </div>
    </div>
  )
}

export default CurrentEvent
