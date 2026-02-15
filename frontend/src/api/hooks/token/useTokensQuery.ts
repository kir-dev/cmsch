import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import type { TokenProgress } from '../../../util/views/token.view'
import { QueryKeys } from '../queryKeys'

export const useTokensQuery = () => {
  return useQuery<TokenProgress, Error>({
    queryKey: [QueryKeys.TOKEN],
    queryFn: async () => {
      const response = await axios.get<TokenProgress>(ApiPaths.TOKENS)
      return response.data
    }
  })
}
