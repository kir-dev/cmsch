import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'
import { TaskFullDetailsView } from '../../../util/views/task.view'
import { QueryKeys } from '../queryKeys'

export const useTaskFullDetailsQuery = (taskId: string) => {
  return useQuery<TaskFullDetailsView, Error>({
    queryKey: [QueryKeys.TASK_DETAILS, taskId],
    queryFn: async () => {
      const taskDetailsResponse = await axios.get<TaskFullDetailsView>(joinPath(ApiPaths.TASK_SUBMIT, taskId))
      if (!taskDetailsResponse.data.task) {
        throw new Error()
      }
      return taskDetailsResponse.data
    }
  })
}
