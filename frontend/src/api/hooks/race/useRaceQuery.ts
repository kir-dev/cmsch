import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { RaceView } from '@/util/views/race.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useRaceQuery = (category: string) => {
  return useQuery<RaceView, Error>({
    queryKey: [QueryKeys.RACE, category],
    queryFn: async () => {
      const response = await axios.get<RaceView>(joinPath(ApiPaths.RACE, category))
      return response.data
    }
  })
}
