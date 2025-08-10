import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import { Organization } from '../../../util/views/organization'
import { QueryKeys } from '../queryKeys'

export const useOrganizationList = () => {
  return useQuery<Organization[], Error>({
    queryKey: [QueryKeys.ORGANIZATION],
    queryFn: async () => {
      const response = await axios.get<Organization[]>(ApiPaths.ORGANIZATION)
      return response.data
    }
  })
}
