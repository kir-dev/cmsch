import axios from 'axios'
import { useQuery } from 'react-query'
import { EventListView } from '../../../util/views/event.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useEventListQuery = (onError?: (err: any) => void) => {
  return useQuery<EventListView[], Error>(
    QueryKeys.EVENTS,
    async () => {
      const response = await axios.get<{ allEvents: EventListView[] }>(ApiPaths.EVENTS)
      return response.data.allEvents
    },
    { onError: onError }
  )
}
