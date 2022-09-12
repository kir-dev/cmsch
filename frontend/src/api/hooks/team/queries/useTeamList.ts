import axios from 'axios'
import { useQuery } from 'react-query'
import { TeamListItemView } from '../../../../util/views/team.view'

export const useTeamList = (onError?: (err: any) => void) => {
  return useQuery<TeamListItemView[], Error>(
    ['team', 'list'],
    async () => {
      const response = await axios.get<TeamListItemView[]>(`/api/teams`)
      return response.data
    },
    { onError: onError }
  )
}
