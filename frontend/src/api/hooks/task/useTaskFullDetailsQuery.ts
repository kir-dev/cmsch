import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { TaskFullDetailsView } from '../../../util/views/task.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'
import { joinPath } from '../../../util/core-functions.util'

export const useTaskFullDetailsQuery = (taskId: string) => {
  return useQuery<TaskFullDetailsView, Error>({
    queryKey: [QueryKeys.TASK_DETAILS, taskId],
    queryFn: async () => {
      let taskDetailsResponse = await axios.get<TaskFullDetailsView>(joinPath(ApiPaths.TASK_SUBMIT, taskId))
      if (!taskDetailsResponse.data.task) {
        throw new Error()
      }
      return taskDetailsResponse.data
    }
  })
}
