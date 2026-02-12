import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'
import type { TinderCommunity } from '../../../util/views/tinder.ts'
import { QueryKeys } from '../queryKeys.ts'

export const useTinderCommunity = () => {
  return useQuery<TinderCommunity[], Error>({
    queryKey: [QueryKeys.TINDER],
    queryFn: async () => {
      const response = await axios.get<TinderCommunity[]>(ApiPaths.TINDER)
      return response.data
    }
  })
}
