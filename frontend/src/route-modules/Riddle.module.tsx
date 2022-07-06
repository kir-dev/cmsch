import { lazy } from 'react'
import { Route } from 'react-router-dom'
import RiddleListPage from '../pages/riddle/riddleList.page'
const RiddlePage = lazy(() => import('../pages/riddle/riddle.page'))

export function RiddleModule() {
  return (
    <Route path="riddle">
      <Route path=":id" element={<RiddlePage />} />
      <Route index element={<RiddleListPage />} />
    </Route>
  )
}
