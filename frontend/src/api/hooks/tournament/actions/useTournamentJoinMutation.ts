import { TournamentJoinResponses } from '@/util/views/tournament.view.ts'
import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

export const useTournamentJoinMutation = (onSuccess?: () => void, onError?: () => void) => {
  return useMutation({
    mutationFn: async (id: number) => {
      const response = await axios.put<TournamentJoinResponses>('/api/tournament/register', { id })
      return response.data
    },
    onSuccess,
    onError
  })
}
