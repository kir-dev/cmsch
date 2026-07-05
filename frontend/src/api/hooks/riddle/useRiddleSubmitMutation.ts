import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { RiddleSubmissionResult } from '@/util/views/riddle.view'
import { useMutation } from '@tanstack/react-query'
import axios, { AxiosError } from 'axios'

interface RiddleSubmissionParams {
  solution: string
  id: string
}

export const useRiddleSubmitMutation = (onTooManyRequests: () => void) => {
  return useMutation<RiddleSubmissionResult, Error, RiddleSubmissionParams>({
    mutationKey: [QueryKeys.RIDDLE_SUBMIT],
    mutationFn: async ({ id, solution }: RiddleSubmissionParams) => {
      const url = joinPath(ApiPaths.RIDDLE, 'solve', id)
      try {
        const res = await axios.post(
          url,
          { solution },
          {
            headers: {
              'Content-Type': 'application/json'
            }
          }
        )
        return res.data
      } catch (error) {
        if (error instanceof AxiosError && error.response && error.response.status === 429) {
          onTooManyRequests()
        } else {
          throw error
        }
      }
    }
  })
}
