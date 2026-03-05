import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useEventListQuery } from '@/api/hooks/event/useEventListQuery'
import { isCurrentEvent, useBrandColor, useOpaqueBackground } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import { Link } from 'react-router'
import { PulsingDot } from './PulsingDot'

export default function CurrentEventCard() {
  const { data, error } = useEventListQuery()
  const enableDetailedView = useConfigContext()?.components?.event?.enableDetailedView
  const color = useBrandColor()
  const background = useOpaqueBackground(1)
  if (!data || error) return null
  const currentEvents = data.filter((event) => isCurrentEvent(event))
  if (currentEvents.length === 0) return null

  function isVowel(x: string) {
    return /[aeiouAEIOU]/.test(x)
  }

  return (
    <div className="flex items-center py-2 px-6 m-2 text-left rounded-xl" style={{ color: color, backgroundColor: background }}>
      <PulsingDot className="mr-4" color="#4ade80" />
      <div className="relative bottom-0">
        {isVowel(currentEvents[0].title[0]) ? 'Az ' : 'A '}
        <span className="font-bold">
          {currentEvents.map((event, idx) => (
            <Link key={event.url} to={enableDetailedView ? `${AbsolutePaths.EVENTS}/${event.url}` : AbsolutePaths.EVENTS}>
              {event.title + (idx == currentEvents.length - 1 ? ' ' : ', ')}
            </Link>
          ))}
        </span>
        {currentEvents.length == 1 ? 'éppen most zajlik' : 'események éppen most zajlanak.'}
      </div>
    </div>
  )
}
