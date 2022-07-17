import { Route } from 'react-router-dom'
import { lazy } from 'react'
import { Paths } from '../util/paths'

const HomePage = lazy(() => import('../pages/home/home.page'))

export function HomeModule() {
  return (
    <Route path={Paths.HOME}>
      <Route index element={<HomePage />} />
    </Route>
  )
}
