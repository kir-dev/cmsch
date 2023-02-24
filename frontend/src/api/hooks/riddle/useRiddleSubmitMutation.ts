import axios from 'axios'
import { useMutation } from 'react-query'
import { ApiPaths } from '../../../util/paths'
import { RiddleSubmissonResult } from '../../../util/views/riddle.view'
import { joinPath } from '../../../util/core-functions.util'
import { QueryKeys } from '../queryKeys'

interface RiddleSubmissionParams {
  solution: string
  id: string
}

export const useRiddleSubmitMutation = () => {
  return useMutation<RiddleSubmissonResult, Error, RiddleSubmissionParams>(
    QueryKeys.RIDDLE_SUBMIT,
    async ({ id, solution }: RiddleSubmissionParams) => {
      const res = await axios.post(
        joinPath(ApiPaths.RIDDLE, id),
        { solution },
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      )
      return res.data
    }
  )
}
