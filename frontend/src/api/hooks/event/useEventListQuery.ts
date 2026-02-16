import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import type { EventListView } from '../../../util/views/event.view'
import { QueryKeys } from '../queryKeys'

export const useEventListQuery = () => {
  return useQuery<EventListView[], Error>({
    queryKey: [QueryKeys.EVENTS],
    queryFn: async () => {
      const response = await axios.get<{ allEvents: EventListView[] }>(ApiPaths.EVENTS)
      return response.data.allEvents
    }
  })
}
