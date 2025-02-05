import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { NEW_RIDDLE_ENDPOINTS } from '../../../util/configs/environment.config'
import { ApiPaths } from '../../../util/paths'
import { Hint } from '../../../util/views/riddle.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'

export const useRiddleHintQuery = (id: string) => {
  return useQuery<Hint, Error>({
    queryKey: [QueryKeys.RIDDLE_HINT, id],
    queryFn: async () => {
      const url = NEW_RIDDLE_ENDPOINTS ? joinPath(ApiPaths.RIDDLE, 'solve', id, 'hint') : joinPath(ApiPaths.RIDDLE, id, 'hint')
      const response = await axios.put<Hint>(url)
      return response.data
    },
    enabled: false
  })
}
