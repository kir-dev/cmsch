import axios from 'axios'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { Organization } from '../../../util/views/organization'

export const useOrganizationList = (onError?: (err: any) => void) => {
  return useQuery<Organization[], Error>(
    QueryKeys.ORGANIZATION,
    async () => {
      const response = await axios.get<Organization[]>(ApiPaths.ORGANIZATION)
      return response.data
    },
    { onError: onError }
  )
}
