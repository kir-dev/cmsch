import axios from 'axios'
import { useQuery } from 'react-query'
import { RiddleCategoryHistory } from '../../../util/views/riddle.view'

export const useRiddleHistoryQuery = (onError: (err: any) => void) => {
  return useQuery<RiddleCategoryHistory[], Error>(
    'riddleHistory',
    async () => {
      const response = await axios.get<Object>(`/api/riddle-history`)

      return Object.entries(response.data).map(([key, value]) => ({ categoryName: key, submissions: value }))
    },
    { onError }
  )
}
