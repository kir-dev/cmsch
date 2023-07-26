import { useQuery } from 'react-query'
import { AccessKey } from '../../../util/views/accessKey'
import { QueryKeys } from '../queryKeys'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'

export const useAccessKey = (onError?: (err: any) => void) => {
  return useQuery<AccessKey, Error>(
    QueryKeys.ACCESS_KEY,
    async () => {
      const response = await axios.get<AccessKey>(ApiPaths.ACCESS_KEY)
      return response.data
    },
    { onError: onError, retry: false }
  )
}
