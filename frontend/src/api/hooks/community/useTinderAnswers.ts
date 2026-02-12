import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'
import type { SendAnswerDto } from '../../../util/views/tinder.ts'
import { QueryKeys } from '../queryKeys.ts'

interface TinderAnswerStatus {
  answered: boolean
  answer: SendAnswerDto
}

export const useTinderAnswers = () => {
  return useQuery<TinderAnswerStatus, Error>({
    queryKey: [QueryKeys.TINDER_ANSWERS],
    queryFn: async () => {
      const res = await axios.get<TinderAnswerStatus>(ApiPaths.TINDER_ANSWERS)
      return res.data
    }
  })
}
