import axios from 'axios'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { CommunityListItem } from '../../../util/views/organization'

export const useCommunityList = (onError?: (err: any) => void) => {
  return useQuery<CommunityListItem[], Error>(
    QueryKeys.COMMUNITY,
    async () => {
      const response = await axios.get<CommunityListItem[]>(ApiPaths.COMMUNITY)
      return response.data
    },
    { onError: onError }
  )
}
