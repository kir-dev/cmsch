import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { NEW_RIDDLE_ENDPOINTS } from '@/util/configs/environment.config'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { Riddle } from '@/util/views/riddle.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useRiddleDetailsQuery = (id: string) => {
  return useQuery<Riddle, Error>({
    queryKey: [QueryKeys.RIDDLE, id],
    queryFn: async () => {
      const url = NEW_RIDDLE_ENDPOINTS ? joinPath(ApiPaths.RIDDLE, 'solve', id) : joinPath(ApiPaths.RIDDLE, id)
      const response = await axios.get<Riddle>(url)
      return response.data
    }
  })
}
