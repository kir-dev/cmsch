import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { Organization } from '../../../util/views/organization'
import { joinPath } from '../../../util/core-functions.util'

export const useOrganization = (id: string) => {
  return useQuery<Organization, Error>({
    queryKey: [QueryKeys.ORGANIZATION, id],
    queryFn: async () => {
      const response = await axios.get<Organization>(joinPath(ApiPaths.ORGANIZATION, id))
      return response.data
    }
  })
}
