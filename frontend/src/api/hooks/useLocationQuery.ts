import axios from 'axios'
import { useQuery } from 'react-query'
import { GroupMemberLocationView } from '../../util/views/groupMemberLocation.view'

export const useLocationQuery = (groupName: string | undefined, onError: (err: any) => void) => {
  return useQuery<GroupMemberLocationView[], Error, GroupMemberLocationView[]>(
    ['locations', groupName],
    async () => {
      const response = await axios.get<GroupMemberLocationView[]>(`/api/track/${groupName}`)
      return response.data
    },
    { onError, refetchInterval: 30000 }
  )
}
