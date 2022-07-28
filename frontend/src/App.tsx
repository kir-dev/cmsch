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
import { CmschLayoutSidebar } from './common-components/layout/CmschLayoutSidebar'
import { USE_SIDENAVBAR } from './util/configs/environment.config'

export function App() {
  return (
    <CountdownPage>
      {USE_SIDENAVBAR !== 'false' ? (
        <CmschLayoutSidebar>
          <ComponentsUnderSuspense />
        </CmschLayoutSidebar>
      ) : (
        <CmschLayout>
          <ComponentsUnderSuspense />
        </CmschLayout>
      )}
    </CountdownPage>
  )
}

const ComponentsUnderSuspense = () => (
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
        <Route path="*" element={<ErrorPage message="Hoppá, úgy tűnik egy olyan oldalra került, amely nem létezik többé!" />} />
      </Route>
    </Routes>
  </Suspense>
)
