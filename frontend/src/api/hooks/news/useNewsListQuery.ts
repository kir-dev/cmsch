import axios from 'axios'
import { useQuery } from 'react-query'
import { NewsArticleView } from '../../../util/views/news.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useNewsListQuery = (onError?: (err: any) => void) => {
  return useQuery<NewsArticleView[], Error>(
    QueryKeys.NEWS,
    async () => {
      const response = await axios.get<{ news: NewsArticleView[] }>(ApiPaths.NEWS)
      return response.data.news
    },
    { onError: onError }
  )
}
