import axios from 'axios'
import { useQuery } from 'react-query'
import { ConfigDto } from '../contexts/config/types'

export const useConfigQuery = (onError: (err: any) => void) => {
  return useQuery<ConfigDto, Error, ConfigDto>(
    'config',
    async () => {
      const response = await axios.get<ConfigDto>(`/api/app`)
      return response.data
    },
    { onError: onError }
  )
}
