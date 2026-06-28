import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { ThreadListResponse } from '@/util/views/support.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useSupportThreadsQuery = (enabled = true) => {
  return useQuery<ThreadListResponse, Error>({
    queryKey: [QueryKeys.SUPPORT_THREADS],
    queryFn: async () => {
      const res = await axios.get<ThreadListResponse>(ApiPaths.SUPPORT_THREADS)
      return res.data
    },
    enabled
  })
}
