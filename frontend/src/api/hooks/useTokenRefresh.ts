import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../util/paths'
import { QueryKeys } from './queryKeys.ts'

const queriesToInvalidate = [QueryKeys.USER, QueryKeys.CONFIG] as const

export function useTokenRefresh(onSuccess?: () => void) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => axios.post<void>(ApiPaths.REFRESH),
    onSuccess: async () => {
      for (const queryKey of queriesToInvalidate) {
        await queryClient.invalidateQueries({ queryKey: [queryKey] })
      }

      if (onSuccess) {
        onSuccess()
      }
    }
  })
}
