import axios from 'axios'
import { useQuery } from 'react-query'
import { TaskCategoryFullDetails, TaskFullDetailsView } from '../../util/views/task.view'

export const useTaskFullDetailsQuery = (taskId: string | undefined, onError: (err: any) => void) => {
  return useQuery<TaskFullDetailsView, Error, TaskFullDetailsView>(
    ['taskFullDetails', taskId],
    async () => {
      let taskDetailsResponse = await axios.get<TaskFullDetailsView>(`/api/task/submit/${taskId}`)
      if (!taskDetailsResponse.data.task) {
        throw Error
      }
      const categoryResponse = await axios.get<TaskCategoryFullDetails>(`/api/task/category/${taskDetailsResponse.data.task.categoryId}`)
      taskDetailsResponse.data.task.categoryName = categoryResponse.data.categoryName
      return taskDetailsResponse.data
    },
    { onError }
  )
}
