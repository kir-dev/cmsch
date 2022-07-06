import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'
const CommunitiesPage = lazy(() => import('../pages/communities/communityList.page'))

export function CommunitiesModule() {
  return (
    <Route path={Paths.COMMUNITIES}>
      <Route index element={<CommunitiesPage />} />
    </Route>
  )
}
