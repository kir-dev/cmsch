import { useMutation } from 'react-query'
import axios from 'axios'

import { ApiPaths } from '../../../util/paths'
import { AccessKeyRequest, AccessKeyResponse } from '../../../util/views/accessKey'

export function useAccessKeyMutation(onSuccess: (data: AccessKeyResponse) => void, onError: () => void) {
  return useMutation({
    mutationFn: async (value: AccessKeyRequest) => {
      const response = await axios.post<AccessKeyResponse>(ApiPaths.ACCESS_KEY, value)
      return response.data
    },
    onSuccess,
    onError
  })
}
