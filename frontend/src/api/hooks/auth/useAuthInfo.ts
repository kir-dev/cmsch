import axios from 'axios'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { UserAuthInfoView } from '../../../util/views/authInfo.view.ts'

export const useAuthInfo = () => {
  return useQuery<UserAuthInfoView, Error>(QueryKeys.WHO_AM_I, async () => {
    const response = await axios.get<UserAuthInfoView>(ApiPaths.WHO_AM_I)
    return response.data
  })
}
