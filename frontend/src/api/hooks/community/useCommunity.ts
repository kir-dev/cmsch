import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { Community } from '../../../util/views/organization'
import { joinPath } from '../../../util/core-functions.util'

export const useCommunity = (id: string) => {
  return useQuery<Community, Error>({
    queryKey: [QueryKeys.COMMUNITY, id],
    queryFn: async () => {
      const response = await axios.get<Community>(joinPath(ApiPaths.COMMUNITY, id))
      return response.data
    }
  })
}
