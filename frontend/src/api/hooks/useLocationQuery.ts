import axios from 'axios'
import { useQuery } from 'react-query'
import { GroupMemberLocationView } from '../../util/views/groupMemberLocation.view'
import { QueryKeys } from './queryKeys'
import { ApiPaths } from '../../util/paths'

export const useLocationQuery = (onError: (err: any) => void, onSuccess: () => void) => {
  return useQuery<GroupMemberLocationView[], Error>(
    QueryKeys.LOCATIONS,
    async () => {
      const response = await axios.get<GroupMemberLocationView[]>(ApiPaths.LOCATION)
      return response.data
    },
    { onError, onSuccess, refetchInterval: 15000 }
  )
}
