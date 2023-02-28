import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { ProfileGroupChangePage } from '../pages/profile/profile.groupChange.page'
import { AliasChangePage } from '../pages/profile/profile.aliasChange.page'
import { Paths } from '../util/paths'

const ProfilePage = lazy(() => import('../pages/profile/profile.page'))

export function ProfileModule() {
  return (
    <Route path={Paths.PROFILE}>
      <Route path="change-group" element={<ProfileGroupChangePage />} />
      <Route path="change-alias" element={<AliasChangePage />} />
      <Route index element={<ProfilePage />} />
    </Route>
  )
}
