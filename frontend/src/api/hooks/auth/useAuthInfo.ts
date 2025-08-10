import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import { UserAuthInfoView } from '../../../util/views/authInfo.view.ts'
import { QueryKeys } from '../queryKeys'

export const useAuthInfo = () => {
  return useQuery<UserAuthInfoView, Error>({
    queryKey: [QueryKeys.WHO_AM_I],
    queryFn: async () => {
      const response = await axios.get<UserAuthInfoView>(ApiPaths.WHO_AM_I)
      return response.data
    }
  })
}
