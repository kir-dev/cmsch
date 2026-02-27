import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useEventListQuery } from '@/api/hooks/event/useEventListQuery.ts'
import { LinkButton } from '@/common-components/LinkButton.tsx'
import { Separator } from '@/components/ui/separator'
import { useBrandColor } from '@/util/core-functions.util.ts'
import { AbsolutePaths } from '@/util/paths.ts'
import { useMemo } from 'react'
import { Schedule } from './Schedule.tsx'

const isToday = (timeStamp: number) => new Date(timeStamp).toDateString() === new Date().toDateString()

export default function HomePageEventList() {
  const config = useConfigContext()
  const eventList = useEventListQuery()
  const brandColor = useBrandColor()

  const events = useMemo(() => {
    const timestampCorrectedEventList = eventList.data?.map((li) => {
      const { timestampStart, timestampEnd, ...rest } = li
      return { timestampStart: timestampStart * 1000, timestampEnd: timestampEnd * 1000, ...rest }
    })
    // eslint-disable-next-line react-hooks/purity
    return timestampCorrectedEventList?.filter((li) => li.timestampStart > Date.now()).sort((a, b) => a.timestampStart - b.timestampStart)
  }, [eventList.data])

  const eventsToday = events?.filter((ev) => isToday(ev.timestampStart)) || []
  const eventsLater = events?.filter((ev) => !isToday(ev.timestampStart)).slice(0, 3) || []

  if (!eventList.data) return null
  return (
    <div className="flex flex-col items-center">
      <h2 className="text-2xl font-bold text-center mb-5 mt-20">{config?.components?.event?.title}</h2>
      <div className="flex flex-col items-center space-y-10 w-full">
        <p className="text-center text-3xl font-black mt-10">Mai nap</p>
        {eventsToday.length > 0 ? <Schedule events={eventsToday} /> : <p className="text-center opacity-70 mt-10">Nincs több esemény.</p>}
        <Separator />
        <p className="text-center text-3xl font-black mt-10">Később</p>
        {eventsLater.length > 0 ? (
          <Schedule verbose events={eventsLater} />
        ) : (
          <p className="text-center opacity-70 mt-10">Nincs több esemény.</p>
        )}
        <LinkButton style={{ backgroundColor: brandColor }} href={AbsolutePaths.EVENTS}>
          Részletek
        </LinkButton>
      </div>
    </div>
  )
}
