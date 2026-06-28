import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { SupportThreadEntity } from '@/util/views/support.view'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'

interface CreateThreadRequest {
  title: string
  content: string
  authorName?: string
  authorEmail?: string
}

export const useCreateSupportThreadMutation = () => {
  const queryClient = useQueryClient()
  return useMutation<SupportThreadEntity, Error, CreateThreadRequest>({
    mutationFn: async (req) => {
      const res = await axios.post<SupportThreadEntity>(ApiPaths.SUPPORT_THREAD, req)
      return res.data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QueryKeys.SUPPORT_THREADS] })
    }
  })
}
