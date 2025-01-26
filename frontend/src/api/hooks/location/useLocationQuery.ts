import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { ApiPaths } from '../../../util/paths'
import { MapDataItemView } from '../../../util/views/map.view'
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
