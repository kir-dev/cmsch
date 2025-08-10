import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'
import { RaceView } from '../../../util/views/race.view'
import { QueryKeys } from '../queryKeys'

export const useRaceByTeamQuery = (teamId: string) => {
  return useQuery<RaceView, Error>({
    queryKey: [QueryKeys.RACE, teamId],
    queryFn: async () => {
      const response = await axios.get<RaceView>(joinPath(ApiPaths.RACE_BY_TEAM, teamId))
      return response.data
    }
  })
}
