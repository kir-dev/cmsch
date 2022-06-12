import { Route, Routes } from 'react-router-dom'
import { CmschLayout } from './common-components/layout/CmschLayout'
import './global.css'

export function App() {
  return (
    <CmschLayout>
      <Routes>
        <Route path="/">
          {/*Főoldal*/}
          <Route index element={<IndexPage />} />
          {/*Impressum*/}
          <Route path="impresszum">
            <Route index element={<ImpressumPage />} />
          </Route>
        </Route>
      </Routes>
    </CmschLayout>
  )
}
