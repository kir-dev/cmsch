import axios from 'axios'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { Organization } from '../../../util/views/organization'
import { joinPath } from '../../../util/core-functions.util'

export const useOrganization = (id: string, onError?: (err: any) => void) => {
  return useQuery<Organization, Error>(
    joinPath(QueryKeys.ORGANIZATION, id),
    async () => {
      const response = await axios.get<Organization>(joinPath(ApiPaths.ORGANIZATION, id))
      return response.data
    },
    { onError: onError }
  )
}
