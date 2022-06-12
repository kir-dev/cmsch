import { Route } from 'react-router-dom'
import { RiddlePage } from '../pages/riddle/riddle.page'

export function RiddleModule() {
  return (
    <Route path="riddleok">
      <Route path=":id" element={<RiddlePage />} />
      <Route index element={<RiddlePage />} />
    </Route>
  )
}
