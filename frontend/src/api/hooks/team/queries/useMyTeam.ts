import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { OptionalTeamView } from '../../../../util/views/team.view'
import { QueryKeys } from '../../queryKeys.ts'

export const useMyTeam = () => {
  return useQuery<OptionalTeamView, Error>({
    queryKey: [QueryKeys.TEAM_MY],
    queryFn: async () => {
      const response = await axios.get<OptionalTeamView>(`/api/team/my`)
      return response.data
    }
  })
}
