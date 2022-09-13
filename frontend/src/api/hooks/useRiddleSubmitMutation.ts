import axios from 'axios'
import { useMutation } from 'react-query'
import { Paths } from '../../util/paths'
import { RiddleSubmissonResult } from '../../util/views/riddle.view'

interface RiddleSubmissionParams {
  solution: string
  id: string
}

export const useRiddleSubmitMutation = () => {
  return useMutation<RiddleSubmissonResult, Error, RiddleSubmissionParams>('taskSubmission', async (params: RiddleSubmissionParams) => {
    const res = await axios.post(
      `/api/${Paths.RIDDLE}/${params.id}`,
      { solution: params.solution },
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    )
    return res.data
  })
}
