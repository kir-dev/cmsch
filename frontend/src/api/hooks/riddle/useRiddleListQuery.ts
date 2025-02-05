import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { NEW_RIDDLE_ENDPOINTS } from '../../../util/configs/environment.config'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'
import { RiddleCategory } from '../../../util/views/riddle.view'
import { QueryKeys } from '../queryKeys'

export const useRiddleListQuery = () => {
  return useQuery<RiddleCategory[], Error>({
    queryKey: [QueryKeys.RIDDLE],
    queryFn: async () => {
      const url = NEW_RIDDLE_ENDPOINTS ? joinPath(ApiPaths.RIDDLE, 'categories') : joinPath(ApiPaths.RIDDLE)
      const response = await axios.get<RiddleCategory[]>(url)
      return response.data
    }
  })
}
