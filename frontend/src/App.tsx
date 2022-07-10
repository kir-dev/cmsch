import { Suspense } from 'react'
import { Route, Routes } from 'react-router-dom'
import { CmschLayout } from './common-components/layout/CmschLayout'
import { ErrorPage } from './pages/error/error.page'
import IndexPage from './pages/index/index.page'
import LoginPage from './pages/login/login.page'
import { EnabledModules, GetRoutesForModules } from './util/configs/modules.config'
import CountdownPage from './pages/countdown/countdown.page'

export function App() {
  return (
    <CountdownPage>
      <CmschLayout>
        <Suspense>
          <Routes>
            <Route path="/">
              {GetRoutesForModules(EnabledModules)}
              <Route index element={<IndexPage />} />
              <Route path="login" element={<LoginPage />} />
              <Route path="logout" element={<IndexPage />} />
              {/** Error handling pages */}
              <Route path="error" element={<ErrorPage />} />
              <Route path="*" element={<ErrorPage message="Hoppá, úgy tűnik egy olyan oldalt találtál, amely nem létezik többé!" />} />
            </Route>
          </Routes>
        </Suspense>
      </CmschLayout>
    </CountdownPage>
  )
}
