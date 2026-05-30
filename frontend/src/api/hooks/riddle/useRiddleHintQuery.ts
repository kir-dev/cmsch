import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { Hint } from '@/util/views/riddle.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useRiddleHintQuery = (id: string) => {
  return useQuery<Hint, Error>({
    queryKey: [QueryKeys.RIDDLE_HINT, id],
    queryFn: async () => {
      const url = joinPath(ApiPaths.RIDDLE, 'solve', id, 'hint')
      const response = await axios.put<Hint>(url)
      return response.data
    },
    enabled: false
  })
}
