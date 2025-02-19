import { Route } from 'react-router-dom'
import { Paths } from '../util/paths.ts'
import TournamentPage from '../pages/tournament/tournament.page.tsx'
import TournamentListPage from '../pages/tournament/tournamentList.page.tsx'

export function TournamentModule() {
  return (
    <Route path={Paths.TOURNAMENT}>
      <Route path=":id" element={<TournamentPage />} />
      <Route index element={<TournamentListPage />} />
    </Route>
  )
}
