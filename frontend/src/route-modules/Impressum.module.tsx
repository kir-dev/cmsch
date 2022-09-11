import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const ImpressumPage = lazy(() => import('../pages/impressum/impressum.page'))

export function ImpressumModule() {
  return (
    <Route path={Paths.IMPRESSUM}>
      <Route index element={<ImpressumPage />} />
    </Route>
  )
}
