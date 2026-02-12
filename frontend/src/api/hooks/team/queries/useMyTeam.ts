import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../../util/paths.ts'
import type { OptionalTeamView } from '../../../../util/views/team.view'
import { QueryKeys } from '../../queryKeys.ts'

export const useMyTeam = () => {
  return useQuery<OptionalTeamView, Error>({
    queryKey: [QueryKeys.TEAM_MY],
    queryFn: async () => {
      const response = await axios.get<OptionalTeamView>(ApiPaths.MY_TEAM)
      return response.data
    }
  })
}
