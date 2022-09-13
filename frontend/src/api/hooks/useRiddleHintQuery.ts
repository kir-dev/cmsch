import axios from 'axios'
import { useQuery } from 'react-query'
import { Paths } from '../../util/paths'
import { Hint } from '../../util/views/riddle.view'

export const useRiddleHintQuery = (onError: (err: any) => void, id: string) => {
  return useQuery<Hint, Error, Hint>(
    ['riddleHint', id],
    async () => {
      const response = await axios.put<Hint>(`/api/${Paths.RIDDLE}/${id}/hint`)
      return response.data
    },
    { onError, enabled: false }
  )
}
