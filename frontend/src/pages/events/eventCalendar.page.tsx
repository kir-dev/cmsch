import { Box, Heading } from '@chakra-ui/react'
import { FaArrowLeft } from 'react-icons/fa'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useEventListQuery } from '../../api/hooks/event/useEventListQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../util/paths'
import { DayCalendar } from './components/event-calendar/DayCalendar'
import { WeekCalendar } from './components/event-calendar/WeekCalendar'

function EventCalendarPage() {
  const config = useConfigContext()?.components
  const app = config?.app
  const event = config?.event
  const brandColor = useBrandColor()

  const { isLoading, isError, data } = useEventListQuery()

  if (!event) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={event.title} />

  return (
    <CmschPage>
      <title>{app?.siteName || 'CMSch'} | Naptár</title>
      <LinkButton colorScheme={brandColor} href={AbsolutePaths.EVENTS} leftIcon={<FaArrowLeft />}>
        Vissza a listához
      </LinkButton>
      <Box mb={10}>
        <Heading mb={5}>Naptár</Heading>
        {event.topMessage && <Markdown text={event.topMessage} />}
      </Box>
      <WeekCalendar events={data} />
      <DayCalendar events={data} />
    </CmschPage>
  )
}

export default EventCalendarPage
