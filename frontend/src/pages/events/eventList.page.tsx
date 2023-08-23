import {
  Box,
  Heading,
  Stack,
  Tab,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  useBreakpoint,
  useBreakpointValue,
  useDisclosure
} from '@chakra-ui/react'
import _ from 'lodash'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

import { useEventListQuery } from '../../api/hooks/event/useEventListQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { CardListItem } from './components/CardListItem'
import { EventFilterOption } from './components/EventFilterOption'
import EventList from './components/EventList'
import { FILTER, mapper } from './util/filter'

const EventListPage = () => {
  const { isLoading, isError, data } = useEventListQuery()
  const component = useConfigContext()?.components.event
  const { isOpen, onToggle } = useDisclosure()
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()

  if (!component) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const availableFilters = []
  if (component.filterByCategory) availableFilters.push(FILTER.CATEGORY)
  if (component.filterByLocation) availableFilters.push(FILTER.PLACE)
  if (component.filterByDay) availableFilters.push(FILTER.DAY)

  const pastEvents = data.filter((event) => event.timestampEnd * 1000 < Date.now())
  const upcomingEvents = data.filter((event) => event.timestampEnd * 1000 >= Date.now())

  return (
    <CmschPage>
      <Helmet title={component.title ?? 'Események'} />
      <Box mb={10}>
        <Heading mb={5}>{component.title}</Heading>
        {component.topMessage && <Markdown text={component.topMessage} />}
      </Box>
      <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="soft-rounded" colorScheme="brand">
        {availableFilters.length > 0 && (
          <TabList>
            <Tab>Mind</Tab>
            {component.filterByCategory && <Tab>Kategória szerint</Tab>}
            {component.filterByLocation && <Tab>Helyszín szerint</Tab>}
            {component.filterByDay && <Tab>Időpont szerint</Tab>}
          </TabList>
        )}
        <TabPanels>
          <TabPanel>
            <EventList eventList={upcomingEvents} groupByDay />
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
