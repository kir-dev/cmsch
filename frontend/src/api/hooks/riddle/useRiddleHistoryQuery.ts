import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { NEW_RIDDLE_ENDPOINTS } from '../../../util/configs/environment.config'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'
import { RiddleCategoryHistory } from '../../../util/views/riddle.view'
import { QueryKeys } from '../queryKeys.ts'

export const useRiddleHistoryQuery = () => {
  return useQuery<RiddleCategoryHistory[], Error>({
    queryKey: [QueryKeys.RIDDLE_HISTORY],
    queryFn: async () => {
      const url = NEW_RIDDLE_ENDPOINTS ? joinPath(ApiPaths.RIDDLE, 'history') : joinPath(ApiPaths.RIDDLE_HISTORY)
      const response = await axios.get<Object>(url)

      return Object.entries(response.data).map(([key, value]) => ({ categoryName: key, submissions: value }))
    }
  })
}
