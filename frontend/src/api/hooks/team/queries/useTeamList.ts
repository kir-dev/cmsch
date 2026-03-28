import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths.ts'
import type { TeamListItemView } from '@/util/views/team.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useTeamList = () => {
  return useQuery<TeamListItemView[], Error>({
    queryKey: [QueryKeys.TEAM_LIST],
    queryFn: async () => {
      const response = await axios.get<TeamListItemView[]>(ApiPaths.ALL_TEAMS)
      return response.data
    }
  })
}
