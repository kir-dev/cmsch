import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { ApiPaths } from '../../../util/paths'
import { RaceView } from '../../../util/views/race.view'
import { QueryKeys } from '../queryKeys'

export const useFreestyleRaceQuery = () => {
  return useQuery<RaceView, Error>({
    queryKey: [QueryKeys.FREESTYLE_RACE],
    queryFn: async () => {
      const response = await axios.get<RaceView>(ApiPaths.FREESTYLE_RACE)
      return response.data
    }
  })
}
