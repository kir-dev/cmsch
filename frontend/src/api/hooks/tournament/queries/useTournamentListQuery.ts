import { TournamentPreview } from '../../../../util/views/tournament.view.ts'
import { useQuery } from '@tanstack/react-query'
import { QueryKeys } from '../../queryKeys.ts'
import axios from 'axios'
import { ApiPaths } from '../../../../util/paths.ts'


export const useTournamentListQuery = () => {
  return useQuery<TournamentPreview[], Error>({
    queryKey: [QueryKeys.TOURNAMENTS],
    queryFn: async () => {
      const response = await axios.get<TournamentPreview[]>(ApiPaths.TOURNAMENTS)
      return response.data
    }
  })
}
