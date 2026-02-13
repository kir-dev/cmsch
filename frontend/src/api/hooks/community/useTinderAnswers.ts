import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'
import { QueryKeys } from '../queryKeys.ts'

interface TinderAnswerStatus {
  answered: boolean
  answer: Record<string, string>
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
