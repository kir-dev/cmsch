import axios from 'axios'
import { useQuery } from 'react-query'
import { WarningView } from '../../util/views/warning.view'

export const useWarningQuery = (onError?: (err: any) => void) => {
  return useQuery<WarningView, Error, WarningView>(
    'warning',
    async () => {
      const response = await axios.get<WarningView>(`/api/warning`)
      return response.data
    },
    { onError: onError }
  )
}
