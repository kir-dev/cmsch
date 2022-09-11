import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const RacePage = lazy(() => import('../pages/race/race.page'))

export function RaceModule() {
  return (
    <Route path={Paths.RACE}>
      <Route index element={<RacePage />} />
    </Route>
  )
}
