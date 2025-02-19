import { TournamentPreview } from '../../../util/views/tournament.view.ts'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys.ts'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'


export const useTournamentListQuery = (onError?: (err: any) => void) => {
  return useQuery<TournamentPreview[], Error>(
    QueryKeys.TOURNAMENTS,
    async () => {
      const response = await axios.get<TournamentPreview[]>(ApiPaths.TOURNAMENTS)
      return response.data
    },
    { onError: onError }
  )
}
