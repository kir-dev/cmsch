import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { ThreadDetailResponse } from '@/util/views/support.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useSupportThreadQuery = (uuid: string, secret?: string) => {
  return useQuery<ThreadDetailResponse, Error>({
    queryKey: [QueryKeys.SUPPORT_THREAD, uuid, secret],
    queryFn: async () => {
      const params = secret ? { secret } : {}
      const res = await axios.get<ThreadDetailResponse>(`${ApiPaths.SUPPORT_THREAD}/${uuid}`, { params })
      return res.data
    },
    enabled: !!uuid
  })
}
