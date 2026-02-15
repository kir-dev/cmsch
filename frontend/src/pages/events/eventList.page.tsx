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
import uniq from 'lodash/uniq'
import { FaCalendar } from 'react-icons/fa'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

import { SearchIcon } from '@chakra-ui/icons'
import { createRef, useEffect, useMemo, useState } from 'react'
import { useEventListQuery } from '../../api/hooks/event/useEventListQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CustomTabButton } from '../../common-components/CustomTabButton'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { Paths } from '../../util/paths'
import type { EventListView } from '../../util/views/event.view'
import { CardListItem } from './components/CardListItem'
import { EventFilterOption } from './components/EventFilterOption'
import EventList from './components/EventList'
import { FILTER, mapper } from './util/filter'

const EventListPage = () => {
  const { isLoading, isError, data } = useEventListQuery()
  const config = useConfigContext()?.components
  const event = config?.event
  const app = config?.app
  const { isOpen, onToggle } = useDisclosure()
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  const inputRef = createRef<HTMLInputElement>()
  const [filteredEvents, setFilteredEvents] = useState<EventListView[] | undefined>()
  const brandColor = useBrandColor()

  const availableFilters = []
  if (event?.filterByCategory) availableFilters.push(FILTER.CATEGORY)
  if (event?.filterByLocation) availableFilters.push(FILTER.PLACE)
  if (event?.filterByDay) availableFilters.push(FILTER.DAY)

  // eslint-disable-next-line react-hooks/purity
  const pastEvents = useMemo(() => data?.filter((event) => event.timestampEnd * 1000 < Date.now()), [data])
  // eslint-disable-next-line react-hooks/purity
  const upcomingEvents = useMemo(() => data?.filter((event) => event.timestampEnd * 1000 >= Date.now()), [data])

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
  }, [upcomingEvents])

  if (!event) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={event.title} />
  return (
    <CmschPage>
      <title>
        {app?.siteName || 'CMSch'} | {event.title ?? 'Események'}
      </title>
      <Box mb={5}>
        <Heading as="h1" variant="main-title" mb={5}>
          {event.title}
        </Heading>
        {event.topMessage && <Markdown text={event.topMessage} />}
      </Box>
      <LinkButton colorScheme={brandColor} mb={5} leftIcon={<FaCalendar />} href={Paths.CALENDAR}>
        Megtekintés a naptárban
      </LinkButton>
      <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="soft-rounded" colorScheme={brandColor}>
        {availableFilters.length > 0 && (
          <TabList>
            <CustomTabButton>Mind</CustomTabButton>
            {event.filterByCategory && <CustomTabButton>Kategória szerint</CustomTabButton>}
            {event.filterByLocation && <CustomTabButton>Helyszín szerint</CustomTabButton>}
            {event.filterByDay && <CustomTabButton>Időpont szerint</CustomTabButton>}
          </TabList>
        )}

        <TabPanels>
          <TabPanel p={0}>
            {event.searchEnabled && (
              <InputGroup mt={5}>
                <InputLeftElement h="100%">
                  <SearchIcon />
                </InputLeftElement>
                <Input
                  ref={inputRef}
                  placeholder="Keresés..."
                  size="lg"
                  onChange={handleInput}
                  _placeholder={{ color: 'inherit' }}
                  autoFocus={true}
                />
              </InputGroup>
            )}
            <EventList eventList={filteredEvents || data || []} groupByDay />
          </TabPanel>
          {availableFilters.map((filter) => (
            <TabPanel key={filter} p={0}>
              <Stack>
                <CardListItem title="Mind" open={isOpen} toggle={onToggle} />
                {filter === FILTER.DAY && <EventFilterOption name="Korábbi" events={pastEvents || []} forceOpen={isOpen} />}
                {uniq(upcomingEvents?.map((event) => mapper(filter, event))).map((option) => (
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
