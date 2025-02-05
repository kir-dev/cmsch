import axios from 'axios'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { ApiPaths } from '../../util/paths'
import { QueryKeys } from './queryKeys.ts'

const queriesToInvalidate = [{ queryKey: [QueryKeys.USER] }, { queryKey: [QueryKeys.CONFIG] }]

export function useTokenRefresh(onSuccess?: () => void) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => axios.post<void>(ApiPaths.REFRESH),
    onSuccess: async () => {
      for (const queryKey of queriesToInvalidate) {
        await queryClient.invalidateQueries(queryKey)
      }

      if (onSuccess) {
        onSuccess()
      }
    }
  })
}
