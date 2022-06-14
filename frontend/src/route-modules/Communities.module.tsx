import { lazy } from 'react'
import { Route } from 'react-router-dom'
const CommunitiesPage = lazy(() => import('../pages/communities/communities.page'))

export function CommunitiesModule() {
  return (
    <Route path="korok">
      <Route index element={<CommunitiesPage />} />
    </Route>
  )
}
