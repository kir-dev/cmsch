import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { Organization } from '../../../util/views/organization'

export const useOrganizationList = () => {
  return useQuery<Organization[], Error>({
    queryKey: [QueryKeys.ORGANIZATION],
    queryFn: async () => {
      const response = await axios.get<Organization[]>(ApiPaths.ORGANIZATION)
      return response.data
    }
  })
}
