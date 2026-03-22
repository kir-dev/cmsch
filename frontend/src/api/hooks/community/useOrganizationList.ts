import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { Organization } from '@/util/views/organization'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useOrganizationList = () => {
  return useQuery<Organization[], Error>({
    queryKey: [QueryKeys.ORGANIZATION],
    queryFn: async () => {
      const response = await axios.get<Organization[]>(ApiPaths.ORGANIZATION)
      return response.data
    }
  })
}
