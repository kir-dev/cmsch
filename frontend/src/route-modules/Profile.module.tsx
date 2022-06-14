import { lazy } from 'react'
import { Route } from 'react-router-dom'
const ProfilePage = lazy(() => import('../pages/profile/profile.page'))

export function ProfileModule() {
  return (
    <Route path="profil">
      {/*<Route path="tankor-modositas" element={<div />} />*/}
      <Route index element={<ProfilePage />} />
    </Route>
  )
}
