import axios from 'axios'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { TokenProgress } from '../../../util/views/token.view'

export const useTokensQuery = (onError?: (err: any) => void) => {
  return useQuery<TokenProgress, Error>(
    QueryKeys.TOKEN,
    async () => {
      const response = await axios.get<TokenProgress>(ApiPaths.TOKENS)
      return response.data
    },
    { onError }
  )
}
