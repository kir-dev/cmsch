import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useEventListQuery } from '@/api/hooks/event/useEventListQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CustomTabButton } from '@/common-components/CustomTabButton'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Input } from '@/components/ui/input'
import { Tabs, TabsContent, TabsList } from '@/components/ui/tabs'
import { Paths } from '@/util/paths'
import type { EventListView } from '@/util/views/event.view'
import uniq from 'lodash/uniq'
import { Search } from 'lucide-react'
import { createRef, useEffect, useMemo, useState } from 'react'
import { FaCalendar } from 'react-icons/fa'
import { CardListItem } from './components/CardListItem'
import { EventFilterOption } from './components/EventFilterOption'
import EventList from './components/EventList'
import { Filter, mapper } from './util/filter'

const EventListPage = () => {
  const { isLoading, isError, data } = useEventListQuery()
  const event = useConfigContext()?.components?.event
  const [isFilterOpen, setIsFilterOpen] = useState(false)
  const inputRef = createRef<HTMLInputElement>()
  const [filteredEvents, setFilteredEvents] = useState<EventListView[] | undefined>()

  const availableFilters = []
  if (event?.filterByCategory) availableFilters.push(Filter.CATEGORY)
  if (event?.filterByLocation) availableFilters.push(Filter.PLACE)
  if (event?.filterByDay) availableFilters.push(Filter.DAY)

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
    <CmschPage title={event.title ?? 'Események'}>
      <div className="mb-5">
        <h1 className="mb-5 text-4xl font-bold tracking-tight">{event.title}</h1>
        {event.topMessage && <Markdown text={event.topMessage} />}
      </div>
      <LinkButton className="mb-5" href={Paths.CALENDAR}>
        <FaCalendar className="mr-2" />
        Megtekintés a naptárban
      </LinkButton>

      <Tabs defaultValue="all" className="w-full">
        {availableFilters.length > 0 && (
          <TabsList className="mb-5 flex w-full flex-wrap justify-start">
            <CustomTabButton value="all">Mind</CustomTabButton>
            {event.filterByCategory && <CustomTabButton value="category">Kategória szerint</CustomTabButton>}
            {event.filterByLocation && <CustomTabButton value="location">Helyszín szerint</CustomTabButton>}
            {event.filterByDay && <CustomTabButton value="day">Időpont szerint</CustomTabButton>}
          </TabsList>
        )}

        <TabsContent value="all">
          {event.searchEnabled && (
            <div className="relative mb-5 mt-5 flex items-center">
              <Search className="absolute left-3 h-4 w-4 text-muted-foreground" />
              <Input ref={inputRef} placeholder="Keresés..." className="h-12 pl-10 pr-10" onChange={handleInput} autoFocus={true} />
            </div>
          )}
          <EventList eventList={filteredEvents || data || []} groupByDay />
        </TabsContent>

        {availableFilters.map((filter) => (
          <TabsContent key={filter} value={filter.toLowerCase()}>
            <div className="flex flex-col gap-0">
              <CardListItem title="Mind" open={isFilterOpen} toggle={() => setIsFilterOpen(!isFilterOpen)} />
              {filter === Filter.DAY && <EventFilterOption name="Korábbi" events={pastEvents || []} forceOpen={isFilterOpen} />}
              {uniq(upcomingEvents?.map((event) => mapper(filter, event))).map((option: string) => (
                <EventFilterOption
                  key={option}
                  name={option}
                  events={upcomingEvents?.filter((e) => mapper(filter, e) === option) || []}
                  forceOpen={isFilterOpen}
                />
              ))}
            </div>
          </TabsContent>
        ))}
      </Tabs>
    </CmschPage>
  )
}

export default EventListPage
