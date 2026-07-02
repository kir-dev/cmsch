import { joinPath } from '@/util/core-functions.util.ts'
import { ApiPaths } from '@/util/paths.ts'
import type { OptionalTournamentView } from '@/util/views/tournament.view.ts'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { QueryKeys } from '../../queryKeys.ts'

export const useTournamentQuery = (id: number) => {
  return useQuery<OptionalTournamentView, Error>({
    queryKey: [QueryKeys.TOURNAMENT, id],
    queryFn: async () => {
      const response = await axios.get<OptionalTournamentView>(joinPath(ApiPaths.TOURNAMENTS, id))
      return response.data
    }
  })
}
