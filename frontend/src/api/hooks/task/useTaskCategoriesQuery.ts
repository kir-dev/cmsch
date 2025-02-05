import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { AllTaskCategories, TaskCategoryPreview } from '../../../util/views/task.view'
import { QueryKeys } from '../queryKeys'
import { ApiPaths } from '../../../util/paths'

export const useTaskCategoriesQuery = () => {
  return useQuery<TaskCategoryPreview[], Error>({
    queryKey: [QueryKeys.TASK_CATEGORIES],
    queryFn: async () => {
      const response = await axios.get<AllTaskCategories>(ApiPaths.TASK)
      return response.data.categories
    }
  })
}
