import axios from 'axios'
import { useQuery } from 'react-query'
import { NewsArticleView } from '../../util/views/news.view'

export const useNewsListQuery = (onError: (err: any) => void) => {
  return useQuery<NewsArticleView[], Error, NewsArticleView[]>(
    'news',
    async () => {
      const response = await axios.get<{ news: NewsArticleView[] }>(`/api/news`)
      return response.data.news
    },
    { onError: onError }
  )
}
