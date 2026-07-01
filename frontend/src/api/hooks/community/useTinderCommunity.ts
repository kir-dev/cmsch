import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths.ts'
import type { TinderCommunity } from '@/util/views/tinder.ts'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useTinderCommunity = () => {
  return useQuery<TinderCommunity[], Error>({
    queryKey: [QueryKeys.TINDER],
    queryFn: async () => {
      const response = await axios.get<TinderCommunity[]>(ApiPaths.TINDER)
      return response.data
    }
  })
}
