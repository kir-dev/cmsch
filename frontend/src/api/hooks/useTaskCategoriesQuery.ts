import axios from 'axios'
import { useQuery } from 'react-query'
import { TaskCategoryPreview, AllTaskCategories } from '../../util/views/task.view'

export const useTaskCategoriesQuery = (onError: (err: any) => void) => {
  return useQuery<TaskCategoryPreview[], Error, TaskCategoryPreview[]>(
    'taskCategories',
    async () => {
      const response = await axios.get<AllTaskCategories>(`/api/task`)
      return response.data.categories
    },
    { onError }
  )
}
