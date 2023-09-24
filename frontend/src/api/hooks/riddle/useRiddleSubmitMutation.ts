import axios, { AxiosError } from 'axios'
import { useMutation } from 'react-query'
import { NEW_RIDDLE_ENDPOINTS } from '../../../util/configs/environment.config'
import { ApiPaths } from '../../../util/paths'
import { RiddleSubmissionResult } from '../../../util/views/riddle.view'
import { joinPath } from '../../../util/core-functions.util'
import { QueryKeys } from '../queryKeys'

interface RiddleSubmissionParams {
  solution: string
  id: string
}

export const useRiddleSubmitMutation = (onTooManyRequests: () => void) => {
  return useMutation<RiddleSubmissionResult, Error, RiddleSubmissionParams>(
    QueryKeys.RIDDLE_SUBMIT,
    async ({ id, solution }: RiddleSubmissionParams) => {
      const url = NEW_RIDDLE_ENDPOINTS ? joinPath(ApiPaths.RIDDLE, 'solve', id) : joinPath(ApiPaths.RIDDLE, id)
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
  )
}
