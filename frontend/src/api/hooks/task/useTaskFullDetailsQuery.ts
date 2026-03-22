import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { TaskFullDetailsView } from '@/util/views/task.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useTaskFullDetailsQuery = (taskId: string) => {
  return useQuery<TaskFullDetailsView, Error>({
    queryKey: [QueryKeys.TASK_DETAILS, taskId],
    queryFn: async () => {
      const taskDetailsResponse = await axios.get<TaskFullDetailsView>(joinPath(ApiPaths.TASK_SUBMIT, taskId))
      return taskDetailsResponse.data
    }
  })
}
