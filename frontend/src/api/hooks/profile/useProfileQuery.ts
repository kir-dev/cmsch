import axios from 'axios'
import { useQuery } from 'react-query'
import { ProfileView } from '../../../util/views/profile.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useProfileQuery = (onLoginFailure?: (err: any) => void) => {
  return useQuery<ProfileView, Error>(
    QueryKeys.USER,
    async () => {
      const response = await axios.get<ProfileView>(ApiPaths.PROFILE)
      return response.data
    },
    { onError: onLoginFailure }
  )
}
