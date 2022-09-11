import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'
const QrAreaPage = lazy(() => import('../pages/qr-fight/qrArea.page'))
const QrAreaListPage = lazy(() => import('../pages/qr-fight/qrAreaList.page'))

export function QRFightModule() {
  return (
    <Route path={Paths.QR_FIGHT}>
      <Route path=":id" element={<QrAreaPage />} />
      <Route index element={<QrAreaListPage />} />
    </Route>
  )
}
