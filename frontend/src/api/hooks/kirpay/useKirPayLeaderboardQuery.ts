import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import type { KirPayLeaderboardView } from '@/util/views/kirPayLeaderboard.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useKirPayLeaderboardQuery = (enabled = true) => {
  return useQuery<KirPayLeaderboardView, Error>({
    queryKey: [QueryKeys.KIRPAY_LEADERBOARD],
    queryFn: async () => {
      const response = await axios.get<KirPayLeaderboardView>(joinPath('/api', 'kirpay-leaderboard'))
      return response.data
    },
    enabled
  })
}
