import axios from 'axios'
import { useQuery } from 'react-query'
import { Paths } from '../../util/paths'
import { RiddleCategory } from '../../util/views/riddle.view'

export const useRiddleListQuery = (onError: (err: any) => void) => {
  return useQuery<RiddleCategory[], Error, RiddleCategory[]>(
    'riddleList',
    async () => {
      const response = await axios.get<RiddleCategory[]>(`/api/${Paths.RIDDLE}`)
      return response.data
    },
    { onError }
  )
}
