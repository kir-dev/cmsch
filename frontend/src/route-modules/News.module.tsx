import { lazy } from 'react'
import { Route } from 'react-router-dom'
const NewsPage = lazy(() => import('../pages/news/news.page'))
const NewsListPage = lazy(() => import('../pages/news/newsList.page'))

export function NewsModule() {
  return (
    <Route path="hirek">
      <Route path=":id" element={<NewsPage />} />
      <Route index element={<NewsListPage />} />
    </Route>
  )
}
