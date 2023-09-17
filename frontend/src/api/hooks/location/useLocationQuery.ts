import axios from 'axios'
import { useQuery } from 'react-query'
import { ApiPaths } from '../../../util/paths'
import { MapDataItemView } from '../../../util/views/map.view'
import { QueryKeys } from '../queryKeys'

export const useLocationQuery = (onError?: (err: any) => void, onSuccess?: () => void) => {
  return useQuery<MapDataItemView[], Error>(
    QueryKeys.LOCATIONS,
    async () => {
      const response = await axios.get<MapDataItemView[]>(ApiPaths.LOCATION)
      return response.data
    },
    { onError, onSuccess, refetchInterval: 15000 }
  )
}
