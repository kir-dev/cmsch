import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { EventView } from '../../../util/views/event.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useEventQuery = (path: string) => {
  return useQuery<EventView, Error>({
    queryKey: [QueryKeys.EVENTS, path],
    queryFn: async () => {
      const response = await axios.get<{ event: EventView }>(joinPath(ApiPaths.EVENTS, path))
      return response.data.event
    }
  })
}
