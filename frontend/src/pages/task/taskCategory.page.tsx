import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useTasksInCategoryQuery } from '@/api/hooks/task/useTasksInCategoryQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CustomBreadcrumb } from '@/common-components/CustomBreadcrumb'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown.tsx'
import { PageStatus } from '@/common-components/PageStatus'
import { Badge } from '@/components/ui/badge'
import { AbsolutePaths } from '@/util/paths'
import { Link, Navigate, useParams } from 'react-router'
import { TaskStatusBadge } from './components/TaskStatusBadge'

const TaskCategoryPage = () => {
  const { id } = useParams()
  const { isLoading, isError, data } = useTasksInCategoryQuery(id || 'UNKNOWN')

  const component = useConfigContext()?.components?.task

  if (!id) return <Navigate to={AbsolutePaths.TASKS} />

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const breadcrumbItems = [
    {
      title: component?.title || 'Feladatok',
      to: AbsolutePaths.TASKS
    },
    {
      title: data.categoryName
    }
  ]

  return (
    <CmschPage loginRequired={true} title={data.categoryName}>
      <CustomBreadcrumb items={breadcrumbItems} />
      <h1 className="text-4xl font-bold tracking-tight mb-5">{data.categoryName}</h1>
      {!!data.description && (
        <div className="pb-4">
          <Markdown text={data.description} />
        </div>
      )}
      {data.tasks && data.tasks.length > 0 ? (
        <div className="mt-5 flex flex-col gap-4">
          {data.tasks.map((task) => (
            <div
              key={task.task.id}
              className="rounded-md bg-secondary text-secondary-foreground border px-6 py-2 transition-colors hover:bg-secondary/80"
            >
              <Link to={`${AbsolutePaths.TASKS}/${task.task.id}`}>
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <span className="text-xl font-bold">{task.task.title}</span>
                    {task.task.availableTo < new Date().valueOf() / 1000 && (
                      <Badge className="ml-5" variant="destructive">
                        LEJÁRT
                      </Badge>
                    )}
                  </div>
                  <TaskStatusBadge status={task.status} />
                </div>
              </Link>
            </div>
          ))}
        </div>
      ) : (
        <p>Nincs egyetlen feladat sem ebben a kategóriában.</p>
      )}
    </CmschPage>
  )
}

export default TaskCategoryPage
