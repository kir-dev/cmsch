import { useMutation } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'
import { SendAnswerDto } from '../../../util/views/tinder.ts'
import { QueryKeys } from '../queryKeys.ts'

interface SendAnswerResponse {
  success: boolean
}

export const useTinderAnswerSend = () => {
  return useMutation<SendAnswerResponse, Error, SendAnswerDto>({
    mutationKey: [QueryKeys.TINDER_ANSWER_SUBMIT],
    mutationFn: async (data: SendAnswerDto) => {
      const res = await axios.post(ApiPaths.TINDER_QUESTION, data)
      return res.data
    }
  })
}
