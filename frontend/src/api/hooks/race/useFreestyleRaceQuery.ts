import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { RaceView } from '@/util/views/race.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useFreestyleRaceQuery = () => {
  return useQuery<RaceView, Error>({
    queryKey: [QueryKeys.FREESTYLE_RACE],
    queryFn: async () => {
      const response = await axios.get<RaceView>(ApiPaths.FREESTYLE_RACE)
      return response.data
    }
  })
}
