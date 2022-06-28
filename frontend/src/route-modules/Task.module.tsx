import { lazy } from 'react'
import { Route } from 'react-router-dom'
const TaskPage = lazy(() => import('../pages/task/task.page'))
const TaskListPage = lazy(() => import('../pages/task/taskCategoryList.page'))
const TaskCategoryPage = lazy(() => import('../pages/task/taskCategory.page'))

export function TaskModule() {
  return (
    <Route path="tasks">
      <Route path="category/:id" element={<TaskCategoryPage />} />
      <Route path=":id" element={<TaskPage />} />
      <Route index element={<TaskListPage />} />
    </Route>
  )
}
