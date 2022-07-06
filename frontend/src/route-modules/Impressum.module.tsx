import { lazy } from 'react'
import { Route } from 'react-router-dom'
const ImpressumPage = lazy(() => import('../pages/impressum/impressum.page'))

export function ImpressumModule() {
  return (
    <Route path="impressum">
      <Route index element={<ImpressumPage />} />
    </Route>
  )
}
