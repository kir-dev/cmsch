import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const RacePage = lazy(() => import('../pages/race/race.page'))
const FreestyleRacePage = lazy(() => import('../pages/race/freestyleRace.page'))

export function RaceModule() {
  return (
    <Route path={Paths.RACE}>
      <Route path="freestyle" element={<FreestyleRacePage />} />
      <Route path=":category" element={<RacePage />} />
      <Route index element={<RacePage />} />
    </Route>
  )
}
