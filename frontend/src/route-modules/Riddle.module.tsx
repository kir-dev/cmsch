import { lazy } from 'react'
import { Route } from 'react-router-dom'
const RiddlePage = lazy(() => import('../pages/riddle/riddle.page'))

export function RiddleModule() {
  return (
    <Route path="riddleok">
      <Route path=":id" element={<RiddlePage />} />
      <Route index element={<RiddlePage />} />
    </Route>
  )
}
