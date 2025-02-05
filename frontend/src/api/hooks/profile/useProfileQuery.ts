import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { ProfileView } from '../../../util/views/profile.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useProfileQuery = () => {
  return useQuery<ProfileView, Error>({
    queryKey: [QueryKeys.USER],
    queryFn: async () => {
      const response = await axios.get<ProfileView>(ApiPaths.PROFILE)
      return response.data
    }
  })
}
