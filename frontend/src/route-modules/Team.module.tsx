import { Route } from 'react-router-dom'
import { lazy } from 'react'

import { Paths } from '../util/paths'

const CreateTeamPage = lazy(() => import('../pages/teams/createTeam.page'))
const TeamListPage = lazy(() => import('../pages/teams/teamList.page'))
const TeamDetailsPage = lazy(() => import('../pages/teams/teamDetails.page'))
const MyTeamPage = lazy(() => import('../pages/teams/myTeam.page'))

export function TeamModule() {
  return (
    <>
      <Route path={Paths.CREATE_TEAM} element={<CreateTeamPage />} />
      <Route path={Paths.MY_TEAM} element={<MyTeamPage />} />
      <Route path={Paths.TEAMS}>
        <Route index element={<TeamListPage />} />
        <Route path="details/:id" element={<TeamDetailsPage />} />
      </Route>
    </>
  )
}
