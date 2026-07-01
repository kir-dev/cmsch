import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { NewsArticleView } from '@/util/views/news.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useHomeNews = () => {
  return useQuery<NewsArticleView[], Error>({
    queryKey: [QueryKeys.HOME_NEWS],
    queryFn: async () => {
      const response = await axios.get<NewsArticleView[]>(ApiPaths.HOME_NEWS)
      return response.data
    }
  })
}
