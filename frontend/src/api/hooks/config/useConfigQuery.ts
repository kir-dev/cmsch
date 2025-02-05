import axios, { AxiosError } from 'axios'
import { useQuery } from '@tanstack/react-query'
import { ConfigDto } from '../../contexts/config/types'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useConfigQuery = () => {
  return useQuery<ConfigDto, AxiosError>({
    queryKey: [QueryKeys.CONFIG],
    queryFn: async () => {
      const response = await axios.get<ConfigDto>(ApiPaths.CONFIG)
      return response.data
    }
  })
}
