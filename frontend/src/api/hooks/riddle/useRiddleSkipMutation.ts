import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { RiddleSubmissionResult } from '@/util/views/riddle.view'
import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

export const useRiddleSkipMutation = () => {
  return useMutation<RiddleSubmissionResult, Error, string>({
    mutationKey: [QueryKeys.RIDDLE_SUBMIT],
    mutationFn: async (id: string) => {
      const url = joinPath(ApiPaths.RIDDLE, 'solve', id, 'skip')
      const res = await axios.post(
        url,
        {},
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      )
      return res.data
    }
  })
}
