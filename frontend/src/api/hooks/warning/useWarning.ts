import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import { WarningView } from '../../../util/views/warning.view'
import { QueryKeys } from '../queryKeys'

export const useWarningQuery = () => {
  return useQuery<WarningView, Error, WarningView>({
    queryKey: [QueryKeys.WARNING],
    queryFn: async () => {
      const response = await axios.get<WarningView>(ApiPaths.WARNING)
      return response.data
    },
    refetchOnWindowFocus: true
  })
}
