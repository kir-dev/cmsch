import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import { AllTaskCategories, TaskCategoryPreview } from '../../../util/views/task.view'
import { QueryKeys } from '../queryKeys'

export const useTaskCategoriesQuery = () => {
  return useQuery<TaskCategoryPreview[], Error>({
    queryKey: [QueryKeys.TASK_CATEGORIES],
    queryFn: async () => {
      const response = await axios.get<AllTaskCategories>(ApiPaths.TASK)
      return response.data.categories
    }
  })
}
