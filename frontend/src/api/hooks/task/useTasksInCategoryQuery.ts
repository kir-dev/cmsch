import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { TaskCategoryFullDetails } from '../../../util/views/task.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useTasksInCategoryQuery = (categoryId: string) => {
  return useQuery<TaskCategoryFullDetails, Error>({
    queryKey: [QueryKeys.TASKS_IN_CATEGORY, categoryId],
    queryFn: async () => {
      const response = await axios.get<TaskCategoryFullDetails>(joinPath(ApiPaths.TASK_CATEGORY, categoryId))
      return response.data
    }
  })
}
