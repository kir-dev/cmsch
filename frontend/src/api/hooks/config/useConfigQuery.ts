import axios, { AxiosError } from 'axios'
import { useQuery } from 'react-query'
import { ConfigDto } from '../../contexts/config/types'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useConfigQuery = (onError: (err: AxiosError) => void) => {
  return useQuery<ConfigDto, AxiosError>(
    QueryKeys.CONFIG,
    async () => {
      const response = await axios.get<ConfigDto>(ApiPaths.CONFIG)
      return response.data
    },
    { onError: onError }
  )
}
