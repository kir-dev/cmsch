import axios from 'axios'
import { useQuery } from 'react-query'
import { EventListView } from '../../util/views/event.view'

export const useEventListQuery = (onError: (err: any) => void) => {
  return useQuery<EventListView[], Error, EventListView[]>(
    'events',
    async () => {
      const response = await axios.get<{ allEvents: EventListView[] }>(`/api/events`)
      return response.data.allEvents
    },
    { onError: onError }
  )
}
