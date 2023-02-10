import axios from 'axios'
import { useQuery } from 'react-query'
import { TaskCategoryFullDetails, TaskFullDetailsView } from '../../util/views/task.view'
import { QueryKeys } from './queryKeys'
import { ApiPaths } from '../../util/paths'
import { joinPath } from '../../util/core-functions.util'

export const useTaskFullDetailsQuery = (taskId: string | undefined, onError: (err: any) => void) => {
  return useQuery<TaskFullDetailsView, Error>(
    [QueryKeys.TASK_DETAILS, taskId],
    async () => {
      let taskDetailsResponse = await axios.get<TaskFullDetailsView>(joinPath(ApiPaths.TASK_SUBMIT, taskId))
      if (!taskDetailsResponse.data.task) {
        throw Error
      }
      const categoryResponse = await axios.get<TaskCategoryFullDetails>(
        joinPath(ApiPaths.TASK_CATEGORY, taskDetailsResponse.data.task.categoryId)
      )
      taskDetailsResponse.data.task.categoryName = categoryResponse.data.categoryName
      return taskDetailsResponse.data
    },
    { onError }
  )
}
