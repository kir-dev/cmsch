import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { TaskCategoryFullDetails } from '@/util/views/task.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useTasksInCategoryQuery = (categoryId: string) => {
  return useQuery<TaskCategoryFullDetails, Error>({
    queryKey: [QueryKeys.TASKS_IN_CATEGORY, categoryId],
    queryFn: async () => {
      const response = await axios.get<TaskCategoryFullDetails>(joinPath(ApiPaths.TASK_CATEGORY, categoryId))
      return response.data
    }
  })
}
