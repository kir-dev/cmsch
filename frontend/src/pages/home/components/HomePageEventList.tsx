import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useEventListQuery } from '@/api/hooks/event/useEventListQuery.ts'
import { LinkButton } from '@/common-components/LinkButton.tsx'
import { Separator } from '@/components/ui/separator'
import { useDate } from '@/hooks/useDate.ts'
import { AbsolutePaths } from '@/util/paths.ts'
import { useMemo } from 'react'
import { Schedule } from './Schedule.tsx'

const isToday = (timeStamp: number, now: Date) => new Date(timeStamp).toDateString() === now.toDateString()

export default function HomePageEventList() {
  const config = useConfigContext()
  const eventList = useEventListQuery()
  const now = useDate(10000)
  const events = useMemo(() => {
    const timestampCorrectedEventList = eventList.data?.map((li) => {
      const { timestampStart, timestampEnd, ...rest } = li
      return { timestampStart: timestampStart * 1000, timestampEnd: timestampEnd * 1000, ...rest }
    })
    return timestampCorrectedEventList
      ?.filter((li) => li.timestampStart > now.getTime())
      .sort((a, b) => a.timestampStart - b.timestampStart)
  }, [eventList.data, now])

  const eventsToday = events?.filter((ev) => isToday(ev.timestampStart, now)) || []
  const eventsLater = events?.filter((ev) => !isToday(ev.timestampStart, now)).slice(0, 3) || []

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
        <LinkButton className="bg-primary" href={AbsolutePaths.EVENTS}>
          Részletek
        </LinkButton>
      </div>
    </div>
  )
}
