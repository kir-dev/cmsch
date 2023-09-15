import axios from 'axios'
import { useMutation } from 'react-query'
import { ApiPaths } from '../../../util/paths'
import { RiddleSubmissonResult } from '../../../util/views/riddle.view'
import { joinPath } from '../../../util/core-functions.util'
import { QueryKeys } from '../queryKeys'

export const useRiddleSkipMutation = () => {
  return useMutation<RiddleSubmissonResult, Error, string>(QueryKeys.RIDDLE_SUBMIT, async (id: string) => {
    const res = await axios.post(joinPath(ApiPaths.RIDDLE, id, 'skip'), {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    return res.data
  })
}
