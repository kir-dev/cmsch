import axios from 'axios'
import { TournamentResponses } from '../../../../util/views/tournament.view'
import { useMutation } from '@tanstack/react-query'
import { joinPath } from '../../../../util/core-functions.util.ts'

export const useTournamentJoinMutation = (onSuccess?: () => void, onError?: () => void)=> {
  return useMutation({
    mutationFn: async (id: number) => {
      const response = await axios.post<TournamentResponses>(joinPath('/api/tournament/register',  id))
      return response.data
    },
    onSuccess,
    onError
  })
}
