import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { RaceView } from '../../../util/views/race.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useRaceQuery = (category: string | undefined) => {
  return useQuery<RaceView, Error>({
    queryKey: [QueryKeys.RACE, category],
    queryFn: async () => {
      const response = await axios.get<RaceView>(joinPath(ApiPaths.RACE, category))
      return response.data
    }
  })
}
