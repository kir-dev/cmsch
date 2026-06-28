import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { PublicMessageView as SupportMessageView } from '@/util/views/support.view'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'

interface AddMessageRequest {
  uuid: string
  content: string
  secret?: string
  authorName?: string
}

export const useAddSupportMessageMutation = () => {
  const queryClient = useQueryClient()
  return useMutation<SupportMessageView, Error, AddMessageRequest>({
    mutationFn: async ({ uuid, content, secret, authorName }) => {
      const params = secret ? { secret } : {}
      const res = await axios.post<SupportMessageView>(
        `${ApiPaths.SUPPORT_THREAD}/${uuid}/message`,
        { content, authorName },
        { params }
      )
      return res.data
    },
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: [QueryKeys.SUPPORT_THREAD, variables.uuid] })
    }
  })
}
