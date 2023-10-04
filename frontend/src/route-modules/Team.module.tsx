import { Route } from 'react-router-dom'
import { lazy } from 'react'

import { Paths } from '../util/paths'

const CreateTeamPage = lazy(() => import('../pages/teams/createTeam.page'))
const EditMyTeamPage = lazy(() => import('../pages/teams/editMyTeam.page'))
const TeamListPage = lazy(() => import('../pages/teams/teamList.page'))
const TeamDetailsPage = lazy(() => import('../pages/teams/teamDetails.page'))
const MyTeamPage = lazy(() => import('../pages/teams/myTeam.page'))
const RaceByTeamPage = lazy(() => import('../pages/race/raceByTeam.page'))

export function TeamModule() {
  return (
    <>
      <Route path={Paths.CREATE_TEAM} element={<CreateTeamPage />} />
      <Route path={Paths.EDIT_TEAM} element={<EditMyTeamPage />} />
      <Route path={Paths.MY_TEAM}>
        <Route index element={<MyTeamPage />} />
        <Route path={Paths.RACE} element={<RaceByTeamPage />} />
      </Route>
      <Route path={Paths.TEAMS}>
        <Route index element={<TeamListPage />} />
        <Route path="details/:id">
          <Route index element={<TeamDetailsPage />} />
          <Route path={Paths.RACE} element={<RaceByTeamPage />} />
        </Route>
      </Route>
    </>
  )
}
