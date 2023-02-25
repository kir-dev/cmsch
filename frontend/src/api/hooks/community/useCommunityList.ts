import axios from 'axios'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { Community } from '../../../util/views/organization'

export const useCommunityList = (onError?: (err: any) => void) => {
  return useQuery<Community[], Error>(
    QueryKeys.COMMUNITY,
    async () => {
      const response = await axios.get<Community[]>(ApiPaths.COMMUNITY)
      return response.data
    },
    { onError: onError }
  )
}
