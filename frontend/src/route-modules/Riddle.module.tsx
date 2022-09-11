import { lazy } from 'react'
import { Route } from 'react-router-dom'
import RiddleListPage from '../pages/riddle/riddleList.page'
import { Paths } from '../util/paths'

const RiddlePage = lazy(() => import('../pages/riddle/riddle.page'))

export function RiddleModule() {
  return (
    <Route path={Paths.RIDDLE}>
      <Route path=":id" element={<RiddlePage />} />
      <Route index element={<RiddleListPage />} />
    </Route>
  )
}
