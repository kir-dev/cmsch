import { Helmet } from 'react-helmet-async'
import { Navigate } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useEventListQuery } from '../../api/hooks/useEventListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import EventList from './components/EventList'
import { AbsolutePaths } from '../../util/paths'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { Box, Heading, Stack, Tab, TabList, TabPanel, TabPanels, Tabs, useDisclosure } from '@chakra-ui/react'
import Markdown from '../../common-components/Markdown'
import _ from 'lodash'
import { mapper, FILTER } from './util/filter'
import { EventFilterOption } from './components/EventFilterOption'
import { CardListItem } from './components/CardListItem'

const EventListPage = () => {
  const eventList = useEventListQuery(() => console.log('Event list query failed!'))
  const config = useConfigContext()
  const { sendMessage } = useServiceContext()
  const { isOpen, onToggle } = useDisclosure()

  if (eventList.isLoading) {
    return <Loading />
  }

  if (eventList.isError) {
    sendMessage('Események betöltése sikertelen!\n' + eventList.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof eventList.data === 'undefined') {
    sendMessage('Események betöltése sikertelen!\n Keresse az oldal fejlesztőit.')
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }
  const availableFilters = []
  if (config?.components.event.filterByCategory) availableFilters.push(FILTER.CATEGORY)
  if (config?.components.event.filterByDay) availableFilters.push(FILTER.DAY)
  if (config?.components.event.filterByLocation) availableFilters.push(FILTER.PLACE)

  return (
    <CmschPage>
      <Helmet title="Események" />
      <Box mb={10}>
        <Heading mb={5}>{config?.components.event.title}</Heading>
        {config?.components.event.topMessage && <Markdown text={config?.components.event.topMessage} />}
      </Box>
      <Tabs isFitted variant="enclosed">
        {availableFilters.length > 0 && (
          <TabList>
            <Tab>Minden esemény</Tab>
            {config?.components.event.filterByLocation && <Tab>Helyszín szerint</Tab>}
            {config?.components.event.filterByCategory && <Tab>Kategória szerint</Tab>}
            {config?.components.event.filterByDay && <Tab>Időpont szerint</Tab>}
          </TabList>
        )}
        <TabPanels>
          <TabPanel>
            <EventList eventList={eventList.data} />
          </TabPanel>
          {availableFilters.map((filter) => (
            <TabPanel>
              <Stack>
                <CardListItem title="Mind" open={isOpen} toggle={onToggle} />
                {_.uniq(eventList.data?.map((event) => mapper(filter, event))).map((option) => (
                  <EventFilterOption
                    key={option}
                    name={option}
                    events={eventList.data!!.filter((e) => mapper(filter, e) === option)}
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
