import { lazy } from 'react'
import { Route } from 'react-router-dom'
import { Paths } from '../util/paths'

const NewsPage = lazy(() => import('../pages/news/news.page'))
const NewsListPage = lazy(() => import('../pages/news/newsList.page'))

export function NewsModule() {
  return (
    <Route path={Paths.NEWS}>
      <Route path=":id" element={<NewsPage />} />
      <Route index element={<NewsListPage />} />
    </Route>
  )
}
