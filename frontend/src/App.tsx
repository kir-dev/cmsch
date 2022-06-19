import { Suspense } from 'react'
import { Route, Routes } from 'react-router-dom'
import { CmschLayout } from './common-components/layout/CmschLayout'
import IndexPage from './pages/index/index.page'
import LoginPage from './pages/login/login.page'
import { EnabledModules, GetRoutesForModules } from './util/configs/modules.config'
import { ErrorPage } from './pages/error/error.page'

export function App() {
  return (
    <CmschLayout>
      <Suspense>
        <Routes>
          <Route path="/">
            {GetRoutesForModules(EnabledModules)}
            <Route index element={<IndexPage />} />
            <Route path="login" element={<LoginPage />} />
            <Route path="logout" element={<IndexPage />} />
            <Route path="error" element={<ErrorPage />} />
          </Route>
        </Routes>
      </Suspense>
    </CmschLayout>
  )
}
