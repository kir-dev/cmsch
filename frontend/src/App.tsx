import { Suspense } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { CmschLayout } from './common-components/layout/CmschLayout'
import { ErrorPage } from './pages/error/error.page'
import HomePage from './pages/home/home.page'
import LoginPage from './pages/login/login.page'
import { EnabledModules, GetRoutesForModules } from './util/configs/modules.config'
import CountdownPage from './pages/countdown/countdown.page'
import { useConfigContext } from './api/contexts/config/ConfigContext'

export function App() {
  const config = useConfigContext()
  const defaultPage = config?.components.app.defaultComponent || '/home'
  return (
    <CountdownPage>
      <CmschLayout>
        <Suspense>
          <Routes>
            <Route path="/">
              {GetRoutesForModules(EnabledModules)}
              <Route index element={<Navigate to={defaultPage} />} />
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
