import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useEventListQuery } from '@/api/hooks/event/useEventListQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { AbsolutePaths } from '@/util/paths'
import { ArrowLeft } from 'lucide-react'
import { DayCalendar } from './components/event-calendar/DayCalendar'
import { WeekCalendar } from './components/event-calendar/WeekCalendar'

function EventCalendarPage() {
  const event = useConfigContext()?.components?.event

  const { isLoading, isError, data } = useEventListQuery()

  if (!event) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={event.title} />

  return (
    <CmschPage title="Naptár">
      <LinkButton href={AbsolutePaths.EVENTS} className="flex items-center gap-2 bg-primary text-primary-foreground">
        <ArrowLeft className="h-4 w-4" /> Vissza a listához
      </LinkButton>
      <div className="mb-10">
        <h2 className="text-3xl font-bold font-heading mb-5 mt-5">Naptár</h2>
        {event.topMessage && <Markdown text={event.topMessage} />}
      </div>
      <WeekCalendar events={data} />
      <div className="mt-10">
        <DayCalendar events={data} />
      </div>
    </CmschPage>
  )
}

export default EventCalendarPage
