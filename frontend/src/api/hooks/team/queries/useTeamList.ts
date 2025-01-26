import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { TeamListItemView } from '../../../../util/views/team.view'
import { QueryKeys } from '../../queryKeys.ts'

export const useTeamList = () => {
  return useQuery<TeamListItemView[], Error>({
    queryKey: [QueryKeys.TEAM_LIST],
    queryFn: async () => {
      const response = await axios.get<TeamListItemView[]>(`/api/teams`)
      return response.data
    }
  })
}
