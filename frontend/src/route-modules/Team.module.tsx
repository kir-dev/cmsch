import { Route } from 'react-router-dom'
import { lazy } from 'react'

import { Paths } from '../util/paths'

const TeamAdminPage = lazy(() => import('../pages/teams/teamAdmin.page'))
const CreateTeamPage = lazy(() => import('../pages/teams/createTeam.page'))
const TeamListPage = lazy(() => import('../pages/teams/teamList.page'))
const TeamDetailsPage = lazy(() => import('../pages/teams/teamDetails.page'))
const MyTeamPage = lazy(() => import('../pages/teams/myTeam.page'))
const TeamDashboardPage = lazy(() => import('../pages/teams/teamDashboard.page'))

export function TeamModule() {
  return (
    <>
      <Route path={Paths.CREATE_TEAM} element={<CreateTeamPage />} />
      <Route path={Paths.MY_TEAM} element={<MyTeamPage />} />
      <Route path={Paths.TEAM_DASHBOARD} element={<TeamDashboardPage />} />
      <Route path={Paths.TEAM_ADMIN} element={<TeamAdminPage />} />
      <Route path={Paths.TEAMS}>
        <Route index element={<TeamListPage />} />
        <Route path="details/:id" element={<TeamDetailsPage />} />
      </Route>
    </>
  )
}
