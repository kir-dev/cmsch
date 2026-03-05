import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths.ts'
import type { OptionalTeamView } from '@/util/views/team.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useMyTeam = () => {
  return useQuery<OptionalTeamView, Error>({
    queryKey: [QueryKeys.TEAM_MY],
    queryFn: async () => {
      const response = await axios.get<OptionalTeamView>(ApiPaths.MY_TEAM)
      return response.data
    }
  })
}
