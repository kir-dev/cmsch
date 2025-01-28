import { TournamentView } from '../../../util/views/tournament.view.ts'
import { useQuery } from 'react-query'
import { QueryKeys } from '../queryKeys.ts'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'


export const useTournamentsQuery = (onError?: (err: any) => void) => {
  return useQuery<TournamentView[], Error>(
    QueryKeys.TOURNAMENTS,
    async () => {
      const response = await axios.get<TournamentView[]>(ApiPaths.TOURNAMENTS)
      return response.data
    },
    { onError: onError }
  )
}
