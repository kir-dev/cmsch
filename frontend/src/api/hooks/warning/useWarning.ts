import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { WarningView } from '../../../util/views/warning.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

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
