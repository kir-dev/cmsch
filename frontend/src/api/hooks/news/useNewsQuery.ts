import axios from 'axios'
import { useQuery } from 'react-query'
import { NewsArticleView } from '../../../util/views/news.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useNewsQuery = (id: string, onError?: (err: any) => void) => {
  return useQuery<NewsArticleView, Error>(
    [QueryKeys.NEWS, id],
    async () => {
      const response = await axios.get<NewsArticleView>(joinPath(ApiPaths.NEWS, id))
      return response.data
    },
    { onError: onError }
  )
}
