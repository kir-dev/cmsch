import { lazy } from 'react'
import { Route } from 'react-router-dom'
const TaskPage = lazy(() => import('../pages/task/task.page'))
const TaskListPage = lazy(() => import('../pages/task/taskList.page'))
const TaskCategoryPage = lazy(() => import('../pages/task/taskCategory.page'))

export function TaskModule() {
  return (
    <Route path="bucketlist">
      <Route path="kategoria/:id" element={<TaskPage />} />
      <Route path=":id" element={<TaskCategoryPage />} />
      <Route index element={<TaskListPage />} />
    </Route>
  )
}
