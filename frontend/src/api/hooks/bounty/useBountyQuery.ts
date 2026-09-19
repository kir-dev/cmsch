import { QueryKeys } from '@/api/hooks/queryKeys'
import { ApiPaths } from '@/util/paths'
import type { BountyView } from '@/util/views/bounty.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useBountyQuery = () => {
  return useQuery<BountyView, Error>({
    queryKey: [QueryKeys.BOUNTY],
    queryFn: async () => {
      const response = await axios.get<BountyView>(ApiPaths.BOUNTY)
      return response.data
    },
    refetchInterval: 30_000
  })
}
