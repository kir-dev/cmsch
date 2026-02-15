import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'
import type { GroupChangeDTO } from '../../../util/views/groupChange.view'

export function useGroupChangeMutation(onSuccess: (data: GroupChangeDTO) => void, onError: () => void) {
  return useMutation({
    mutationFn: async (value: string) => {
      const response = await axios.post<GroupChangeDTO>(joinPath(ApiPaths.CHANGE_GROUP, value))
      return response.data
    },
    onSuccess,
    onError
  })
}
