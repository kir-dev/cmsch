import axios from 'axios'
import { useQuery } from 'react-query'
import { NEW_RIDDLE_ENDPOINTS } from '../../../util/configs/environment.config'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'
import { Riddle } from '../../../util/views/riddle.view'
import { QueryKeys } from '../queryKeys'

export const useRiddleDetailsQuery = (id: string, onError?: (err: any) => void) => {
  return useQuery<Riddle, Error>(
    [QueryKeys.RIDDLE, id],
    async () => {
      const url = NEW_RIDDLE_ENDPOINTS ? joinPath(ApiPaths.RIDDLE, 'solve', id) : joinPath(ApiPaths.RIDDLE, id)
      const response = await axios.get<Riddle>(url)
      return response.data
    },
    { onError }
  )
}
