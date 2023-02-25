import axios from 'axios'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { Community } from '../../../util/views/organization'
import { joinPath } from '../../../util/core-functions.util'

export const useCommunity = (id: string, onError?: (err: any) => void) => {
  return useQuery<Community, Error>(
    joinPath(QueryKeys.COMMUNITY, id),
    async () => {
      const response = await axios.get<Community>(joinPath(ApiPaths.COMMUNITY, id))
      return response.data
    },
    { onError: onError }
  )
}
