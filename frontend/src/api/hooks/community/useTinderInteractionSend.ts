import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths.ts'
import type { TinderInteractionDto } from '@/util/views/tinder.ts'
import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

interface TinderInteractionResponse {
  success: boolean
}

export const useTinderInteractionSend = () => {
  return useMutation<TinderInteractionResponse, Error, TinderInteractionDto>({
    mutationKey: [QueryKeys.TINDER_INTERACTION],
    mutationFn: async (data: TinderInteractionDto) => {
      const res = await axios.post(ApiPaths.TINDER_INTERACTION, data)
      return res.data
    }
  })
}
