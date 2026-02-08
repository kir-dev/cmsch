import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { joinPath } from '../../../../util/core-functions.util.ts'
import { ApiPaths } from '../../../../util/paths.ts'
import type { OptionalTeamView } from '../../../../util/views/team.view'
import { QueryKeys } from '../../queryKeys.ts'

export const useTeamDetails = (id: string) => {
  return useQuery<OptionalTeamView, Error>({
    queryKey: [QueryKeys.TEAM_DETAILS, id],
    queryFn: async () => {
      if (!id) throw new Error('Nincs ID!')
      const response = await axios.get<OptionalTeamView>(joinPath(ApiPaths.TEAM, id))
      return response.data
    }
  })
}
