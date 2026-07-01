import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { LinkButton } from '@/common-components/LinkButton'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { formatHu, stringifyTimeRange } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { EventListView } from '@/util/views/event.view'
import type { RefObject } from 'react'

export type EventBoxItem = EventListView & { top: number; bottom: number; width: number; left: number }

interface EventBoxProps {
  boxRef?: RefObject<HTMLDivElement | null>
  event: EventBoxItem
}

export function EventBox({ event, boxRef }: EventBoxProps) {
  const component = useConfigContext()?.components?.event
  const isShort = 100 - event.top - event.bottom < 5
  return (
    <Popover>
      <PopoverTrigger asChild>
        <div
          ref={boxRef}
          className={
            'overflow-hidden absolute rounded-md p-1 border cursor-pointer ' +
            'text-primary-foreground dark:text-primary-foreground border-primary bg-primary'
          }
          style={{
            left: event.left + '%',
            width: event.width + '%',
            top: event.top + '%',
            bottom: event.bottom + '%',

            paddingTop: isShort ? 0 : 4
          }}
        >
          <p className="text-sm font-bold break-all leading-[1.1]">{event.title}</p>
          <p className="opacity-50 whitespace-nowrap overflow-hidden">
            {formatHu(event.timestampStart, 'HH:mm')} - {formatHu(event.timestampEnd, 'HH:mm')}
          </p>
        </div>
      </PopoverTrigger>
      <PopoverContent className="w-64 p-4">
        <h3 className="font-bold truncate">{event.title}</h3>
        <p className="mt-2 text-sm text-muted-foreground">{stringifyTimeRange(event.timestampStart, event.timestampEnd)}</p>
        {component?.enableDetailedView && (
          <div className="mt-4 flex justify-end">
            <LinkButton href={`${AbsolutePaths.EVENTS}/${event.url}`}>Részletek</LinkButton>
          </div>
        )}
      </PopoverContent>
    </Popover>
  )
}
