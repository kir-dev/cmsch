import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { AccessKeyRequest, AccessKeyResponse } from '@/util/views/accessKey'

export function useAccessKeyMutation(onSuccess: (data: AccessKeyResponse) => void, onError: () => void) {
  return useMutation({
    mutationKey: [QueryKeys.ACCESS_KEY],
    mutationFn: async (value: AccessKeyRequest) => {
      const response = await axios.post<AccessKeyResponse>(ApiPaths.ACCESS_KEY, value)
      return response.data
    },
    onSuccess,
    onError
  })
}
