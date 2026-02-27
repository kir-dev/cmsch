import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { TokenProgress } from '@/util/views/token.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useTokensQuery = () => {
  return useQuery<TokenProgress, Error>({
    queryKey: [QueryKeys.TOKEN],
    queryFn: async () => {
      const response = await axios.get<TokenProgress>(ApiPaths.TOKENS)
      return response.data
    }
  })
}
