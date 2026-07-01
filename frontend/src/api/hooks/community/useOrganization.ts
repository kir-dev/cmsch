import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { Organization } from '@/util/views/organization'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useOrganization = (id: string) => {
  return useQuery<Organization, Error>({
    queryKey: [QueryKeys.ORGANIZATION, id],
    queryFn: async () => {
      const response = await axios.get<Organization>(joinPath(ApiPaths.ORGANIZATION, id))
      return response.data
    }
  })
}
