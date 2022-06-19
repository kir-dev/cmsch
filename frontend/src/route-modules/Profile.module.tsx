import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { ProfileGroupChangePage } from '../pages/profile/profile.groupChange.page'
const ProfilePage = lazy(() => import('../pages/profile/profile.page'))

export function ProfileModule() {
  return (
    <Route path="profil">
      <Route path="tankor-modositas" element={<ProfileGroupChangePage />} />
      <Route index element={<ProfilePage />} />
    </Route>
  )
}
