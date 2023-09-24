import axios from 'axios'
import { useMutation } from 'react-query'
import { NEW_RIDDLE_ENDPOINTS } from '../../../util/configs/environment.config'
import { ApiPaths } from '../../../util/paths'
import { RiddleSubmissionResult } from '../../../util/views/riddle.view'
import { joinPath } from '../../../util/core-functions.util'
import { QueryKeys } from '../queryKeys'

export const useRiddleSkipMutation = () => {
  return useMutation<RiddleSubmissionResult, Error, string>(QueryKeys.RIDDLE_SUBMIT, async (id: string) => {
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
  })
}
