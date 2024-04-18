import axios from 'axios'
import { useQuery } from 'react-query'
import { OptionalTeamView } from '../../../../util/views/team.view'

export const useTeamDetails = (id?: string, onError?: (err: any) => void) => {
  return useQuery<OptionalTeamView, Error>(
    ['team', 'details', id],
    async () => {
      if (!id) throw new Error('Nincs ID!')
      const response = await axios.get<OptionalTeamView>(`/api/team/${id}`)
      return response.data
    },
    { onError: onError }
  )
}
