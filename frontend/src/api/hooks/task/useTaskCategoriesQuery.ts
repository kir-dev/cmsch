import axios from 'axios'
import { useQuery } from 'react-query'
import { AllTaskCategories, TaskCategoryPreview } from '../../../util/views/task.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useTaskCategoriesQuery = (onError?: (err: any) => void) => {
  return useQuery<TaskCategoryPreview[], Error>(
    QueryKeys.TASK_CATEGORIES,
    async () => {
      const response = await axios.get<AllTaskCategories>(ApiPaths.TASK)
      return response.data.categories
    },
    { onError }
  )
}
