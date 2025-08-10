import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import { NewsArticleView } from '../../../util/views/news.view'
import { QueryKeys } from '../queryKeys'

export const useHomeNews = () => {
  return useQuery<NewsArticleView[], Error>({
    queryKey: [QueryKeys.HOME_NEWS],
    queryFn: async () => {
      const response = await axios.get<NewsArticleView[]>(ApiPaths.HOME_NEWS)
      return response.data
    }
  })
}
