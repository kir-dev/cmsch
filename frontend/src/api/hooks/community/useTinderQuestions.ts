import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths.ts'
import { TinderQuestion } from '../../../util/views/tinder.ts'
import { QueryKeys } from '../queryKeys.ts'


export const useTinderQuestions = () => {
  return useQuery<TinderQuestion[], Error>({
    queryKey: [QueryKeys.TINDER_QUESTION],
    queryFn: async () => {
      const response = await axios.get<TinderQuestion[]>(ApiPaths.TINDER_QUESTION)
      return response.data
    }
  })
}
