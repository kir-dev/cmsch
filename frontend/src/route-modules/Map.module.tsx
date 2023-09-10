import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const MapPage = lazy(() => import('../pages/map/map.page'))

export function MapModule() {
  return (
    <Route path={Paths.MAP}>
      <Route index element={<MapPage />} />
    </Route>
  )
}
