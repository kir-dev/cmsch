import axios from 'axios'
import { useQuery } from 'react-query'
import { ConfigDto } from '../../contexts/config/types'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useConfigQuery = (onError: (err: any) => void) => {
  return useQuery<ConfigDto, Error>(
    QueryKeys.CONFIG,
    async () => {
      const response = await axios.get<ConfigDto>(ApiPaths.CONFIG)
      return response.data
    },
    { onError: onError }
  )
}
