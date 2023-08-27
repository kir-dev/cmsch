import { Box, Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { FaArrowLeft } from 'react-icons/fa'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useEventListQuery } from '../../api/hooks/event/useEventListQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { AbsolutePaths } from '../../util/paths'
import { DayCalendar } from './components/event-calendar/DayCalendar'
import { WeekCalendar } from './components/event-calendar/WeekCalendar'

function EventCalendarPage() {
  const component = useConfigContext()?.components.event

  const { isLoading, isError, data } = useEventListQuery()

  if (!component) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage>
      <Helmet title="Naptár" />
      <LinkButton href={AbsolutePaths.EVENTS} leftIcon={<FaArrowLeft />}>
        Vissza a listához
      </LinkButton>
      <Box mb={10}>
        <Heading mb={5}>Naptár</Heading>
        {component.topMessage && <Markdown text={component.topMessage} />}
      </Box>
      <WeekCalendar events={data} />
      <DayCalendar events={data} />
    </CmschPage>
  )
}

export default EventCalendarPage
