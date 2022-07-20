import { Suspense } from 'react'
import { Route, Routes } from 'react-router-dom'
import { CmschLayout } from './common-components/layout/CmschLayout'
import { ErrorPage } from './pages/error/error.page'
import HomePage from './pages/home/home.page'
import LoginPage from './pages/login/login.page'
import { EnabledModules, GetRoutesForModules } from './util/configs/modules.config'
import CountdownPage from './pages/countdown/countdown.page'
import { MetaTags } from './metaTags'
import IndexPage from './pages/index/index.page'

export function App() {
  return (
    <CountdownPage>
      <CmschLayout>
        <Suspense>
          <MetaTags />
          <Routes>
            <Route path="/">
              {GetRoutesForModules(EnabledModules)}
              <Route index element={<IndexPage />} />
              <Route path="login" element={<LoginPage />} />
              <Route path="logout" element={<HomePage />} />
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
