import axios from 'axios'
import { useQuery } from 'react-query'
import { NewsArticleView } from '../../util/views/news.view'

export const useNewsQuery = (id: string, onError: (err: any) => void) => {
  return useQuery<NewsArticleView, Error, NewsArticleView>(
    ['newsItem', id],
    async () => {
      const response = await axios.get<NewsArticleView>(`/api/news/${id}`)
      return response.data
    },
    { onError: onError }
  )
}
