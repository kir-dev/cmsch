import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths.ts'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

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
