import axios from 'axios'
import { useQuery } from 'react-query'
import { GroupMemberLocationView } from '../../util/views/groupMemberLocation.view'

export const useLocationQuery = (onError: (err: any) => void, onSuccess: () => void) => {
  return useQuery<GroupMemberLocationView[], Error, GroupMemberLocationView[]>(
    'locations',
    async () => {
      const response = await axios.get<GroupMemberLocationView[]>(`/api/track-my-group`)
      return response.data
    },
    { onError, onSuccess, refetchInterval: 15000 }
  )
}
