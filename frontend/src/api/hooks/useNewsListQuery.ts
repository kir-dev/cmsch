import axios from 'axios'
import { useQuery } from 'react-query'
import { NewsView } from '../../util/views/news.view'

export const useNewsListQuery = (onError: (err: any) => void) => {
  return useQuery<NewsView, Error, NewsView>(
    'news',
    async () => {
      const response = await axios.get<NewsView>(`/api/news`)
      return response.data
    },
    { onError: onError }
  )
}
