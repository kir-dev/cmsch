import axios from 'axios'
import { useQuery } from 'react-query'
import { OptionalTeamView } from '../../../../util/views/team.view'

export const useMyTeam = (onError?: (err: any) => void) => {
  return useQuery<OptionalTeamView, Error>(
    ['team', 'my'],
    async () => {
      const response = await axios.get<OptionalTeamView>(`/api/team/my`)
      return response.data
    },
    { onError: onError }
  )
}
