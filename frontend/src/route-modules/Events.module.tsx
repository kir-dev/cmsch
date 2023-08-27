import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const EventPage = lazy(() => import('../pages/events/event.page'))
const EventListPage = lazy(() => import('../pages/events/eventList.page'))
const EventCalendarPage = lazy(() => import('../pages/events/eventCalendar.page'))

export function EventsModule() {
  return (
    <Route path={Paths.EVENTS}>
      <Route path=":path" element={<EventPage />} />
      <Route path={Paths.CALENDAR} element={<EventCalendarPage />} />
      <Route index element={<EventListPage />} />
    </Route>
  )
}
