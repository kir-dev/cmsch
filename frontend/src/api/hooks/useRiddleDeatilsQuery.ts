import axios from 'axios'
import { useQuery } from 'react-query'
import { Paths } from '../../util/paths'
import { Riddle } from '../../util/views/riddle.view'

export const useRiddleDetailsQuery = (onError: (err: any) => void, id: string) => {
  return useQuery<Riddle, Error, Riddle>(
    ['riddleDetails', id],
    async () => {
      const response = await axios.get<Riddle>(`/api/${Paths.RIDDLE}/${id}`)
      return response.data
    },
    { onError }
  )
}
