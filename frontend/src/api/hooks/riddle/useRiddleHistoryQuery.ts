import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { RiddleCategoryHistory } from '@/util/views/riddle.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useRiddleHistoryQuery = () => {
  return useQuery<RiddleCategoryHistory[], Error>({
    queryKey: [QueryKeys.RIDDLE_HISTORY],
    queryFn: async () => {
      const url = joinPath(ApiPaths.RIDDLE, 'history')
      const response = await axios.get<object>(url)

      return Object.entries(response.data).map(([key, value]) => ({ categoryName: key, submissions: value }))
    }
  })
}
