import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { AccessKey } from '@/util/views/accessKey'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useAccessKey = () => {
  return useQuery<AccessKey, Error>({
    retry: false,
    queryKey: [QueryKeys.ACCESS_KEY],
    queryFn: async () => {
      const response = await axios.get<AccessKey>(ApiPaths.ACCESS_KEY)
      return response.data
    }
  })
}
