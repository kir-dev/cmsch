import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { NewsArticleView } from '../../../util/views/news.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useNewsListQuery = () => {
  return useQuery<NewsArticleView[], Error>({
    queryKey: [QueryKeys.NEWS],
    queryFn: async () => {
      const response = await axios.get<{ news: NewsArticleView[] }>(ApiPaths.NEWS)
      return response.data.news
    }
  })
}
