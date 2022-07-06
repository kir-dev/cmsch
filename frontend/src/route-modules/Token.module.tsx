import { Route } from 'react-router-dom'
import { lazy } from 'react'
import { Paths } from '../util/paths'

const TokenScanResultPage = lazy(() => import('../pages/token/tokenScanResult.page'))
const TokenListPage = lazy(() => import('../pages/token/tokenList.page'))
const TokenScanPage = lazy(() => import('../pages/token/tokenScan.page'))

export function TokenModule() {
  return (
    <>
      <Route path={Paths.TOKEN_SCANNED} element={<TokenScanResultPage />} />
      <Route path={Paths.TOKEN}>
        <Route index element={<TokenListPage />} />
        <Route path="scan" element={<TokenScanPage />} />
      </Route>
    </>
  )
}
