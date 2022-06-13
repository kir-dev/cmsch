import { Route } from 'react-router-dom'
import { TaskPage } from '../pages/task/task.page'
import { TaskListPage } from '../pages/task/taskList.page'
import { TaskCategoryPage } from '../pages/task/taskCategory.page'

export function TaskModule() {
  return (
    <Route path="bucketlist">
      <Route path="kategoria/:id" element={<TaskPage />} />
      <Route path=":id" element={<TaskCategoryPage />} />
      <Route index element={<TaskListPage />} />
    </Route>
  )
}
