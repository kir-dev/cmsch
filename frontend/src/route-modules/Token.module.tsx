import { Route } from 'react-router-dom'
import { lazy } from 'react'

const TokenScanResultPage = lazy(() => import('../pages/token/tokenScanResult.page'))
const TokenListPage = lazy(() => import('../pages/token/tokenList.page'))
const TokenScanPage = lazy(() => import('../pages/token/tokenScan.page'))

export function TokenModule() {
  return (
    <>
      <Route path="qr-scanned" element={<TokenScanResultPage />} />
      <Route path="qr">
        <Route index element={<TokenListPage />} />
        <Route path="scan" element={<TokenScanPage />} />
      </Route>
    </>
  )
}
