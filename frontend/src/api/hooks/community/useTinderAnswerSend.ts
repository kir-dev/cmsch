import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'
import type { SendAnswerDto } from '../../../util/views/tinder.ts'
import { QueryKeys } from '../queryKeys.ts'

interface SendAnswerResponse {
  success: boolean
}

export const useTinderAnswerSend = () => {
  const queryClient = useQueryClient()
  return useMutation<SendAnswerResponse, Error, SendAnswerDto>({
    mutationKey: [QueryKeys.TINDER_ANSWER_SUBMIT],
    mutationFn: async (data: SendAnswerDto) => (await axios.post(ApiPaths.TINDER_QUESTION, data)).data,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: [QueryKeys.TINDER] })
  })
}
