import axios from 'axios'
import { useQuery } from 'react-query'
import { ProfileView } from '../../util/views/profile.view'

export const useProfileQuery = (onLoginFailure: (err: any) => void) => {
  return useQuery<ProfileView, Error, ProfileView>(
    'currentUser',
    async () => {
      const response = await axios.get<ProfileView>(`/api/profile`)
      return response.data
    },
    { onError: onLoginFailure }
  )
}
