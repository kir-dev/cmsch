import { ApiPaths } from '@/util/paths.ts'
import type { TournamentPreview } from '@/util/views/tournament.view.ts'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { QueryKeys } from '../../queryKeys.ts'

export const useTournamentListQuery = () => {
  return useQuery<TournamentPreview[], Error>({
    queryKey: [QueryKeys.TOURNAMENT],
    queryFn: async () => {
      const response = await axios.get<TournamentPreview[]>(ApiPaths.TOURNAMENTS)
      return response.data
    }
  })
}
