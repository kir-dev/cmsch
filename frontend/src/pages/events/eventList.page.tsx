import { Helmet } from 'react-helmet-async'
import { Navigate } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useEventListQuery } from '../../api/hooks/event/useEventListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import EventList from './components/EventList'
import { AbsolutePaths } from '../../util/paths'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { Box, Heading, Stack, TabList, TabPanel, TabPanels, Tabs, useBreakpoint, useBreakpointValue, useDisclosure } from '@chakra-ui/react'
import Markdown from '../../common-components/Markdown'
import _ from 'lodash'
import { FILTER, mapper } from './util/filter'
import { EventFilterOption } from './components/EventFilterOption'
import { CardListItem } from './components/CardListItem'
import { CustomTab } from './components/CustomTab'
import { l } from '../../util/language'
import { LoadingPage } from '../loading/loading.page'

const EventListPage = () => {
  const eventList = useEventListQuery(() => console.log('Event list query failed!'))
  const config = useConfigContext()
  const { sendMessage } = useServiceContext()
  const { isOpen, onToggle } = useDisclosure()
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()

  if (eventList.isLoading) {
    return <LoadingPage />
  }

  if (eventList.isError) {
    sendMessage(l('event-load-failed') + eventList.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof eventList.data === 'undefined') {
    sendMessage(l('event-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }
  const availableFilters = []
  if (config?.components.event.filterByCategory) availableFilters.push(FILTER.CATEGORY)
  if (config?.components.event.filterByLocation) availableFilters.push(FILTER.PLACE)
  if (config?.components.event.filterByDay) availableFilters.push(FILTER.DAY)

  const pastEvents = eventList.data.filter((event) => event.timestampEnd * 1000 < Date.now())
  const upcomingEvents = eventList.data.filter((event) => event.timestampEnd * 1000 >= Date.now())

  return (
    <CmschPage>
      <Helmet title="Események" />
      <Box mb={10}>
        <Heading mb={5}>{config?.components.event.title}</Heading>
        {config?.components.event.topMessage && <Markdown text={config?.components.event.topMessage} />}
      </Box>
      <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="unstyled">
        {availableFilters.length > 0 && (
          <TabList>
            <CustomTab>Mind</CustomTab>
            {config?.components.event.filterByCategory && <CustomTab>Kategória szerint</CustomTab>}
            {config?.components.event.filterByLocation && <CustomTab>Helyszín szerint</CustomTab>}
            {config?.components.event.filterByDay && <CustomTab>Időpont szerint</CustomTab>}
          </TabList>
        )}
        <TabPanels>
          <TabPanel>
            <EventList eventList={upcomingEvents} />
          </TabPanel>
          {availableFilters.map((filter) => (
            <TabPanel key={filter}>
              <Stack>
                <CardListItem title="Mind" open={isOpen} toggle={onToggle} />
                {filter === FILTER.DAY && <EventFilterOption name="Korábbi" events={pastEvents} forceOpen={isOpen} />}
                {_.uniq(upcomingEvents.map((event) => mapper(filter, event))).map((option) => (
                  <EventFilterOption
                    key={option}
                    name={option}
                    events={upcomingEvents.filter((e) => mapper(filter, e) === option)}
                    forceOpen={isOpen}
                  />
                ))}
              </Stack>
            </TabPanel>
          ))}
        </TabPanels>
      </Tabs>
    </CmschPage>
  )
}

export default EventListPage
