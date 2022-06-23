import axios from 'axios'
import { useQuery } from 'react-query'
import { TaskCategoryPreview } from '../../util/views/task.view'

export const useTaskCategoriesQuery = (isLoggedIn: boolean, onLoginFailure: (err: any) => void) => {
  return useQuery<TaskCategoryPreview[], Error, TaskCategoryPreview[]>(
    'taskCategories',
    async () => {
      const response = await axios.get<TaskCategoryPreview[]>(`/api/task`)
      return response.data
    },
    { enabled: isLoggedIn, onError: onLoginFailure }
  )
}
