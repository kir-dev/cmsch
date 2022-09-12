import axios from 'axios'
import { useQuery } from 'react-query'
import { TeamView } from '../../../../util/views/team.view'

export const useTeamDetails = (id?: string, onError?: (err: any) => void) => {
  return useQuery<TeamView, Error>(
    ['team', 'details', id],
    async () => {
      if (!id) throw new Error('Nincs ID!')
      const response = await axios.get<TeamView>(`/api/team/${id}`)
      return response.data
    },
    { onError: onError }
  )
}
