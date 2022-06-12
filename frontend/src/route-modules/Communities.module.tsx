import { Route } from 'react-router-dom'
import { CommunitiesPage } from '../pages/communities/communities.page'

export function CommunitiesModule() {
  return (
    <Route path="korok">
      <Route index element={<CommunitiesPage />} />
    </Route>
  )
}
