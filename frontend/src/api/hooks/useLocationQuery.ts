import axios from 'axios'
import { useQuery } from 'react-query'
import { GroupMemberLocationView } from '../../util/views/groupMemberLocation.view'

export const useLocationQuery = (onError: (err: any) => void) => {
  return useQuery<GroupMemberLocationView[], Error, GroupMemberLocationView[]>(
    'locations',
    async () => {
      const response = await axios.get<GroupMemberLocationView[]>(`/api/track-my-group`)
      return response.data
    },
    { onError, refetchInterval: 30000 }
  )
}
