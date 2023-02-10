import axios from 'axios'
import { useQuery } from 'react-query'
import { ApiPaths } from '../../util/paths'
import { Hint } from '../../util/views/riddle.view'
import { QueryKeys } from './queryKeys'
import { joinPath } from '../../util/core-functions.util'

export const useRiddleHintQuery = (onError: (err: any) => void, id: string) => {
  return useQuery<Hint, Error>(
    [QueryKeys.RIDDLE_HINT, id],
    async () => {
      const response = await axios.put<Hint>(joinPath(ApiPaths.RIDDLE, id, 'hint'))
      return response.data
    },
    { onError, enabled: false }
  )
}
