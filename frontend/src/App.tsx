import { Route, Routes } from 'react-router-dom'
import { CmschLayout } from './common-components/layout/CmschLayout'
import { IndexPage } from './pages/index/index.page'
import { EnabledModules, GetRoutesForModules } from './util/configs/modules.config'

export function App() {
  return (
    <CmschLayout>
      <Routes>
        <Route path="/">
          {GetRoutesForModules(EnabledModules)}
          <Route index element={<IndexPage />} />
        </Route>
      </Routes>
    </CmschLayout>
  )
}
