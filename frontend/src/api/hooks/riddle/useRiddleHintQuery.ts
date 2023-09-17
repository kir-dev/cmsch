import axios from 'axios'
import { useQuery } from 'react-query'
import { NEW_RIDDLE_ENDPOINTS } from '../../../util/configs/environment.config'
import { ApiPaths } from '../../../util/paths'
import { Hint } from '../../../util/views/riddle.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'

export const useRiddleHintQuery = (id: string, onError?: (err: any) => void) => {
  return useQuery<Hint, Error>(
    [QueryKeys.RIDDLE_HINT, id],
    async () => {
      const url = NEW_RIDDLE_ENDPOINTS ? joinPath(ApiPaths.RIDDLE, 'solve', id, 'hint') : joinPath(ApiPaths.RIDDLE, id, 'hint')
      const response = await axios.put<Hint>(url)
      return response.data
    },
    { onError, enabled: false }
  )
}
