import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useTaskCategoriesQuery } from '@/api/hooks/task/useTaskCategoriesQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { TaskCategoryType } from '@/util/views/task.view'
import { TaskCategoryListItem } from './components/TaskCategoryListIem'

const TaskCategoryListPage = () => {
  const component = useConfigContext()?.components?.task

  const { isLoading, isError, data } = useTaskCategoriesQuery()

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const normalCategories = data.filter((c) => c.type == TaskCategoryType.REGULAR)
  const prCategories = data.filter((c) => c.type == TaskCategoryType.PROFILE_REQUIRED)

  const required = prCategories.length > 0 && (
    <div className="mb-10">
      <h1 className="mb-5 text-4xl font-bold tracking-tight">{component.profileRequiredTitle}</h1>
      {component.profileRequiredMessage && <Markdown text={component.profileRequiredMessage} />}
      <div className="mt-5 flex flex-col gap-4">
        {prCategories.map((category) => (
          <TaskCategoryListItem key={category.categoryId} category={category} />
        ))}
      </div>
    </div>
  )

  return (
    <CmschPage loginRequired={true} title={component?.title}>
      {required}
      <h1 className="mb-5 text-4xl font-bold tracking-tight">{component.regularTitle}</h1>
      {component.regularMessage && <Markdown text={component.regularMessage} />}
      {normalCategories.length > 0 ? (
        <div className="mt-5 flex flex-col gap-4">
          {normalCategories.map((category) => (
            <TaskCategoryListItem key={category.categoryId} category={category} />
          ))}
        </div>
      ) : (
        <p>Nincs egyetlen feladat sem.</p>
      )}
    </CmschPage>
  )
}

export default TaskCategoryListPage
