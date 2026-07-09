import { TournamentCancelResponses } from '@/util/views/tournament.view.ts'
import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

export const useTournamentCancelMutation = (onSuccess?: () => void, onError?: () => void) => {
  return useMutation({
    mutationFn: async (id: number) => {
      const response = await axios.put<TournamentCancelResponses>('/api/tournament/unregister', { id })
      return response.data
    },
    onSuccess,
    onError
  })
}
