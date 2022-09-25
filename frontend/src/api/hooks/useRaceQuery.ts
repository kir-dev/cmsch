import axios from 'axios'
import { useQuery } from 'react-query'
import { RaceView } from '../../util/views/race.view'

export const useRaceQuery = (category: String, onError: (err: any) => void) => {
  return useQuery<RaceView, Error, RaceView>(
    ['race', category],
    async () => {
      const response = await axios.get<RaceView>(`/api/race/${category}`)
      return response.data
    },
    { onError }
  )
}
