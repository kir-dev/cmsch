import axios from 'axios'
import { useQuery } from 'react-query'
import { TeamView } from '../../../../util/views/team.view'

export const useMyTeam = (onError?: (err: any) => void) => {
  return useQuery<TeamView, Error>(
    ['team', 'my'],
    async () => {
      const response = await axios.get<TeamView>(`/api/team/my`)
      return response.data
    },
    { onError: onError }
  )
}
