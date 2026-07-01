import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { NEW_RIDDLE_ENDPOINTS } from '@/util/configs/environment.config'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { RiddleSubmissionResult } from '@/util/views/riddle.view'
import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

export const useRiddleSkipMutation = () => {
  return useMutation<RiddleSubmissionResult, Error, string>({
    mutationKey: [QueryKeys.RIDDLE_SUBMIT],
    mutationFn: async (id: string) => {
      const url = NEW_RIDDLE_ENDPOINTS ? joinPath(ApiPaths.RIDDLE, 'solve', id, 'skip') : joinPath(ApiPaths.RIDDLE, id, 'skip')
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
