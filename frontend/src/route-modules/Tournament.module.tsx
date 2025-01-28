import { Route } from 'react-router-dom'
import { Paths } from '../util/paths.ts'
import TournamentListPage from '../pages/tournament/TournamentList.page.tsx'

export function TournamentModule() {
  return (
    <Route path={Paths.TOURNAMENT}>
      {/*<Route path=":id" element={<TournamentPage />} />*/}
      <Route index element={<TournamentListPage />} />
    </Route>
  )
}
