import axios from 'axios'
import { useQuery } from 'react-query'
import { ApiPaths } from '../../../util/paths'
import { RaceView } from '../../../util/views/race.view'
import { QueryKeys } from '../queryKeys'

export const useFreestyleRaceQuery = (onError?: (err: any) => void) => {
  return useQuery<RaceView, Error>(
    [QueryKeys.FREESTYLE_RACE],
    async () => {
      const response = await axios.get<RaceView>(ApiPaths.FREESTYLE_RACE)
      return response.data
    },
    { onError }
  )
}
