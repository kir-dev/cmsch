import { useMutation } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'
import type { TinderInteractionDto } from '../../../util/views/tinder.ts'
import { QueryKeys } from '../queryKeys.ts'

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
