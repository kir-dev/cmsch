import axios from 'axios'
import { useQuery } from 'react-query'
import { ApiPaths } from '../../../util/paths'
import { NewsArticleView } from '../../../util/views/news.view'
import { QueryKeys } from '../queryKeys'

export const useHomeNews = (onError?: (err: any) => void) => {
  return useQuery<NewsArticleView[], Error>(
    [QueryKeys.HOME_NEWS],
    async () => {
      const response = await axios.get<NewsArticleView[]>(ApiPaths.HOME_NEWS)
      return response.data
    },
    { onError: onError }
  )
}
