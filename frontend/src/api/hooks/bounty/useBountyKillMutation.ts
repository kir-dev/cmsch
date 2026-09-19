import { QueryKeys } from '@/api/hooks/queryKeys'
import { ApiPaths } from '@/util/paths'
import type { BountyKillResponse } from '@/util/views/bounty.view'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'

export function useBountyKillMutation(onSuccess?: (response: BountyKillResponse) => void) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async (qrData: string) => {
      let code
      try {
        code = new URL(qrData).searchParams.get('code')
      } catch (e) {
        console.error(e)
      }
      code = code || qrData.trim()
      if (!code) throw new Error()
      const response = await axios.post<BountyKillResponse>(ApiPaths.BOUNTY_KILL, { code })
      return response.data
    },
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: [QueryKeys.BOUNTY] })
      onSuccess?.(response)
    }
  })
}
