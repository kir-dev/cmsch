import { NewsArticleView } from '../../../util/views/news.view'

export const sortNewsList = (newsList: NewsArticleView[]) =>
  newsList.sort((a, b) => (a.highlighted === b.highlighted ? 0 : a.highlighted ? -1 : 1))
