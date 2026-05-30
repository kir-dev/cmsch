import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { Riddle } from '@/util/views/riddle.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useRiddleDetailsQuery = (id: string) => {
  return useQuery<Riddle, Error>({
    queryKey: [QueryKeys.RIDDLE, id],
    queryFn: async () => {
      const url = joinPath(ApiPaths.RIDDLE, 'solve', id)
      const response = await axios.get<Riddle>(url)
      return response.data
    }
  })
}
