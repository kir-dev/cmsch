import axios from 'axios'
import { useQuery } from 'react-query'
import { ApiPaths } from '../../util/paths'
import { Riddle } from '../../util/views/riddle.view'
import { QueryKeys } from './queryKeys'
import { joinPath } from '../../util/core-functions.util'

export const useRiddleDetailsQuery = (onError: (err: any) => void, id: string) => {
  return useQuery<Riddle, Error>(
    [QueryKeys.RIDDLE, id],
    async () => {
      const response = await axios.get<Riddle>(joinPath(ApiPaths.RIDDLE, id))
      return response.data
    },
    { onError }
  )
}
