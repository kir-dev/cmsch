import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { RiddleCategory } from '@/util/views/riddle.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useRiddleListQuery = () => {
  return useQuery<RiddleCategory[], Error>({
    queryKey: [QueryKeys.RIDDLE],
    queryFn: async () => {
      const url = joinPath(ApiPaths.RIDDLE, 'categories')
      const response = await axios.get<RiddleCategory[]>(url)
      return response.data
    }
  })
}
