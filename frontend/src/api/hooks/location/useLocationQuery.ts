import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import type { MapDataItemView } from '../../../util/views/map.view'
import { QueryKeys } from '../queryKeys'

export const useLocationQuery = () => {
  return useQuery<MapDataItemView[], Error>({
    queryKey: [QueryKeys.LOCATIONS],
    queryFn: async () => {
      const response = await axios.get<MapDataItemView[]>(ApiPaths.LOCATION)
      return response.data
    },
    refetchInterval: 15000
  })
}
