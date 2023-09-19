import axios from 'axios'
import { useQuery } from 'react-query'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'
import { RaceView } from '../../../util/views/race.view'
import { QueryKeys } from '../queryKeys'

export const useRaceByTeamQuery = (teamId: string, onError?: (err: any) => void) => {
  return useQuery<RaceView, Error>(
    [QueryKeys.RACE, teamId],
    async () => {
      const response = await axios.get<RaceView>(joinPath(ApiPaths.RACE_BY_TEAM, teamId))
      return response.data
    },
    { onError }
  )
}
