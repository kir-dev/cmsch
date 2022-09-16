import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'
const QrLevelListPage = lazy(() => import('../pages/qr-fight/qrLevels.page'))

export function QRFightModule() {
  return <Route path={Paths.QR_FIGHT} element={<QrLevelListPage />} />
}
