import axios from 'axios'
import { useQuery } from 'react-query'
import { WarningView } from '../../../util/views/warning.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useWarningQuery = (onError?: (err: any) => void) => {
  return useQuery<WarningView, Error, WarningView>(
    QueryKeys.WARNING,
    async () => {
      const response = await axios.get<WarningView>(ApiPaths.WARNING)
      return response.data
    },
    { onError, refetchOnWindowFocus: true }
  )
}
