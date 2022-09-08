import { Route } from 'react-router-dom'
import { lazy } from 'react'

import { Paths } from '../util/paths'
const CreateTeamPage = lazy(() => import('../pages/teams/createTeam.page'))
const TeamListPage = lazy(() => import('../pages/teams/teamList.page'))
const TeamDetailsPage = lazy(() => import('../pages/teams/teamDetails.page'))

export function TeamModule() {
  return (
    <>
      <Route path={Paths.TEAM}>
        <Route index element={<TeamListPage />} />
        <Route path="create" element={<CreateTeamPage />} />
        <Route path="details/:id" element={<TeamDetailsPage />} />
      </Route>
    </>
  )
}
