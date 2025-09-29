import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../../util/paths.ts'
import { TournamentPreview } from '../../../../util/views/tournament.view.ts'
import { QueryKeys } from '../../queryKeys.ts'

export const useTournamentListQuery = () => {
  return useQuery<TournamentPreview[], Error>({
    queryKey: [QueryKeys.TOURNAMENTS],
    queryFn: async () => {
      const response = await axios.get<TournamentPreview[]>(ApiPaths.TOURNAMENTS)
      return response.data
    }
  })
}
