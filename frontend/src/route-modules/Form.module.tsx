import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const FormPage = lazy(() => import('../pages/form/form.page'))

export function FormModule() {
  return (
    <Route path={Paths.FORM + '/:slug'}>
      <Route index element={<FormPage />} />
    </Route>
  )
}
