import { Route } from 'react-router-dom'
import { NewsPage } from '../pages/news/news.page'
import { NewsListPage } from '../pages/news/newsList.page'

export function NewsModule() {
  return (
    <Route path="hirek">
      <Route path=":id" element={<NewsPage />} />
      <Route index element={<NewsListPage />} />
    </Route>
  )
}
