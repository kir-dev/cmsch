import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import type { NewsArticleView } from '../../../util/views/news.view'
import { QueryKeys } from '../queryKeys'

export const useNewsListQuery = () => {
  return useQuery<NewsArticleView[], Error>({
    queryKey: [QueryKeys.NEWS],
    queryFn: async () => {
      const response = await axios.get<{ news: NewsArticleView[] }>(ApiPaths.NEWS)
      return response.data.news
    }
  })
}
