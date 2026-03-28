import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { NewsArticleView } from '@/util/views/news.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useNewsQuery = (id: string) => {
  return useQuery<NewsArticleView, Error>({
    queryKey: [QueryKeys.NEWS, id],
    queryFn: async () => {
      const response = await axios.get<NewsArticleView>(joinPath(ApiPaths.NEWS, id))
      return response.data
    }
  })
}
