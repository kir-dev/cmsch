import axios from 'axios'
import { useQuery } from 'react-query'
import { RaceView } from '../../../util/views/race.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useRaceQuery = (category: string | undefined, onError?: (err: any) => void) => {
  return useQuery<RaceView, Error>(
    [QueryKeys.RACE, category],
    async () => {
      const response = await axios.get<RaceView>(joinPath(ApiPaths.RACE, category))
      return response.data
    },
    { onError }
  )
}
