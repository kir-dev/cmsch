import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { TokenProgress } from '../../../util/views/token.view'

export const useTokensQuery = () => {
  return useQuery<TokenProgress, Error>({
    queryKey: [QueryKeys.TOKEN],
    queryFn: async () => {
      const response = await axios.get<TokenProgress>(ApiPaths.TOKENS)
      return response.data
    }
  })
}
