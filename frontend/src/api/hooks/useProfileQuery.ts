import axios from 'axios'
import { useQuery } from 'react-query'
import { ProfileView } from '../../util/views/profile.view'

export const useProfileQuery = (isLoggedIn: boolean, onLoginFailure: (err: any) => void) => {
  return useQuery<ProfileView, Error, ProfileView>(
    'currentUser',
    async () => {
      const response = await axios.get<ProfileView>(`/api/profile`)
      return response.data
    },
    { enabled: isLoggedIn, onError: onLoginFailure }
  )
}
