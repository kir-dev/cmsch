import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { TeamListItemView } from '../../../../util/views/team.view'
import { QueryKeys } from '../../queryKeys.ts'
import { ApiPaths } from '../../../../util/paths.ts'

export const useTeamList = () => {
  return useQuery<TeamListItemView[], Error>({
    queryKey: [QueryKeys.TEAM_LIST],
    queryFn: async () => {
      const response = await axios.get<TeamListItemView[]>(ApiPaths.ALL_TEAMS)
      return response.data
    }
  })
}
