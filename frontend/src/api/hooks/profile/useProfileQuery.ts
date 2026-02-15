import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import type { ProfileView } from '../../../util/views/profile.view'
import { QueryKeys } from '../queryKeys'

export const useProfileQuery = () => {
  return useQuery<ProfileView, Error>({
    queryKey: [QueryKeys.USER],
    queryFn: async () => {
      const response = await axios.get<ProfileView>(ApiPaths.PROFILE)
      return response.data
    }
  })
}
