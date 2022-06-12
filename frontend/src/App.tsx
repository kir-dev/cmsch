import { Route, Routes } from 'react-router-dom'
import { CmschLayout } from './common-components/layout/CmschLayout'
import './global.css'
import { IndexPage } from './pages/index/IndexPage'

export function App() {
  return (
    <CmschLayout>
      <Routes>
        <Route path="/">
          {/*Főoldal*/}
          <Route index element={<IndexPage />} />
        </Route>
      </Routes>
    </CmschLayout>
  )
}
