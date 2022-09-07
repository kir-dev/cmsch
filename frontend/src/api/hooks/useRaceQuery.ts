import axios from 'axios'
import { useQuery } from 'react-query'
import { RaceView } from '../../util/views/race.view'

export const useRaceQuery = (onError: (err: any) => void) => {
  return useQuery<RaceView, Error, RaceView>(
    'race',
    async () => {
      const response = await axios.get<RaceView>(`/api/race`)
      return response.data
    },
    { onError }
  )
}
