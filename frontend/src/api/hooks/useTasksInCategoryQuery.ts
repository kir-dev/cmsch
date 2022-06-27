import axios from 'axios'
import { useQuery } from 'react-query'
import { TaskCategoryFullDetails } from '../../util/views/task.view'

export const useTasksInCategoryQuery = (categoryId: string | undefined, onError: (err: any) => void) => {
  return useQuery<TaskCategoryFullDetails, Error, TaskCategoryFullDetails>(
    ['tasksInCategory', categoryId],
    async () => {
      const response = await axios.get<TaskCategoryFullDetails>(`/api/task/category/${categoryId}`)
      return response.data
    },
    { onError }
  )
}
