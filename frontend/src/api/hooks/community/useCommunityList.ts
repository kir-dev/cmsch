import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { Community } from '../../../util/views/organization'

export const useCommunityList = () => {
  return useQuery<Community[], Error>({
    queryKey: [QueryKeys.COMMUNITY],
    queryFn: async () => {
      const response = await axios.get<Community[]>(ApiPaths.COMMUNITY)
      return response.data
    }
  })
}
