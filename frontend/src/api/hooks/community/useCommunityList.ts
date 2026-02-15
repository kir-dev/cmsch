import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import type { Community } from '../../../util/views/organization'
import { QueryKeys } from '../queryKeys'

export const useCommunityList = () => {
  return useQuery<Community[], Error>({
    queryKey: [QueryKeys.COMMUNITY],
    queryFn: async () => {
      const response = await axios.get<Community[]>(ApiPaths.COMMUNITY)
      return response.data
    }
  })
}
