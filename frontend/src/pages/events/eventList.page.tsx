import {
  Box,
  Heading,
  Input,
  InputGroup,
  InputLeftElement,
  Stack,
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
import { FaCalendar } from 'react-icons/fa'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

import { useEventListQuery } from '../../api/hooks/event/useEventListQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { Paths } from '../../util/paths'
import { CardListItem } from './components/CardListItem'
import { EventFilterOption } from './components/EventFilterOption'
import EventList from './components/EventList'
import { FILTER, mapper } from './util/filter'
import { CustomTabButton } from '../../common-components/CustomTabButton'
import { SearchIcon } from '@chakra-ui/icons'
import { createRef, useEffect, useState } from 'react'
import { EventListView } from '../../util/views/event.view'

const EventListPage = () => {
  const { isLoading, isError, data } = useEventListQuery()
  const component = useConfigContext()?.components.event
  const { isOpen, onToggle } = useDisclosure()
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  const inputRef = createRef<HTMLInputElement>()
  const [filteredEvents, setFilteredEvents] = useState<EventListView[] | undefined>()

  if (!component) return <ComponentUnavailable />

  const availableFilters = []
  if (component.filterByCategory) availableFilters.push(FILTER.CATEGORY)
  if (component.filterByLocation) availableFilters.push(FILTER.PLACE)
  if (component.filterByDay) availableFilters.push(FILTER.DAY)

  const pastEvents = data?.filter((event) => event.timestampEnd * 1000 < Date.now())
  const upcomingEvents = data?.filter((event) => event.timestampEnd * 1000 >= Date.now())

  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!data) {
      setFilteredEvents(undefined)
    } else if (!search) {
      setFilteredEvents(upcomingEvents)
    } else {
      setFilteredEvents(upcomingEvents?.filter((event) => event.title.toLowerCase().includes(search)))
    }
  }

  useEffect(() => {
    setFilteredEvents(upcomingEvents)
  }, [data])

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />
  return (
    <CmschPage>
      <Helmet title={component.title ?? 'Események'} />
      <Box mb={5}>
        <Heading mb={5}>{component.title}</Heading>
        {component.topMessage && <Markdown text={component.topMessage} />}
      </Box>
      <LinkButton mb={5} leftIcon={<FaCalendar />} href={Paths.CALENDAR}>
        Megtekintés a naptárban
      </LinkButton>
      <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="soft-rounded" colorScheme="brand">
        {availableFilters.length > 0 && (
          <TabList>
            <CustomTabButton>Mind</CustomTabButton>
            {component.filterByCategory && <CustomTabButton>Kategória szerint</CustomTabButton>}
            {component.filterByLocation && <CustomTabButton>Helyszín szerint</CustomTabButton>}
            {component.filterByDay && <CustomTabButton>Időpont szerint</CustomTabButton>}
          </TabList>
        )}

        <TabPanels>
          <TabPanel p={0}>
            {component.searchEnabled && (
              <InputGroup mt={5}>
                <InputLeftElement h="100%">
                  <SearchIcon />
                </InputLeftElement>
                <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
              </InputGroup>
            )}
            <EventList eventList={filteredEvents || data || []} groupByDay />
          </TabPanel>
          {availableFilters.map((filter) => (
            <TabPanel key={filter} p={0}>
              <Stack>
                <CardListItem title="Mind" open={isOpen} toggle={onToggle} />
                {filter === FILTER.DAY && <EventFilterOption name="Korábbi" events={pastEvents || []} forceOpen={isOpen} />}
                {_.uniq(upcomingEvents?.map((event) => mapper(filter, event))).map((option) => (
                  <EventFilterOption
                    key={option}
                    name={option}
                    events={upcomingEvents?.filter((e) => mapper(filter, e) === option) || []}
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
