import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const ExtraPage = lazy(() => import('../pages/extra/extra.page'))

export function ExtraPageModule() {
  return (
    <Route path={Paths.EXTRA_PAGE + '/:slug'}>
      <Route index element={<ExtraPage />} />
    </Route>
  )
}
