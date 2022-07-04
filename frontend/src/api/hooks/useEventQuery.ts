import axios from 'axios'
import { useQuery } from 'react-query'
import { EventView } from '../../util/views/event.view'

export const useEventQuery = (path: string, onError: (err: any) => void) => {
  return useQuery<EventView, Error, EventView>(
    ['event', path],
    async () => {
      const response = await axios.get<{ event: EventView }>(`/api/events/${path}`)
      return response.data.event
    },
    { onError: onError }
  )
}
