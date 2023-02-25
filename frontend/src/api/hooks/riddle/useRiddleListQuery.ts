import axios from 'axios'
import { useQuery } from 'react-query'
import { ApiPaths } from '../../../util/paths'
import { RiddleCategory } from '../../../util/views/riddle.view'
import { QueryKeys } from '../queryKeys'

export const useRiddleListQuery = (onError?: (err: any) => void) => {
  return useQuery<RiddleCategory[], Error>(
    QueryKeys.RIDDLE,
    async () => {
      const response = await axios.get<RiddleCategory[]>(ApiPaths.RIDDLE)
      return response.data
    },
    { onError }
  )
}
