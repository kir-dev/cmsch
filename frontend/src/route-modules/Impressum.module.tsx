import { Route } from 'react-router-dom'
import { ImpressumPage } from '../pages/impressum/impressum.page'

export function ImpressumModule() {
  return (
    <Route path="impresszum">
      <Route index element={<ImpressumPage />} />
    </Route>
  )
}
