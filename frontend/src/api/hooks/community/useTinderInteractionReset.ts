import { ApiPaths } from '@/util/paths.ts'
import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

interface TinderInteractionResponse {
  success: boolean
}

export const useTinderInteractionReset = () => {
  return useMutation<TinderInteractionResponse, Error>({
    mutationKey: ['tinderInteractionReset'],
    mutationFn: async () => {
      const res = await axios.post(`${ApiPaths.TINDER_INTERACTION}/reset`)
      return res.data
    }
  })
}
