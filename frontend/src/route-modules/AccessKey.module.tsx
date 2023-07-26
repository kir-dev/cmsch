import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const AccessKeyPage = lazy(() => import('../pages/access-key/accessKey.page'))

export function AccessKeyModule() {
  return <Route path={Paths.ACCESS_KEY} element={<AccessKeyPage />} />
}
