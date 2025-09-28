import { Heading, Text, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useTaskCategoriesQuery } from '../../api/hooks/task/useTaskCategoriesQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { taskCategoryType } from '../../util/views/task.view'
import { TaskCategoryListItem } from './components/TaskCategoryListIem'

const TaskCategoryListPage = () => {
  const component = useConfigContext()?.components?.task

  const { isLoading, isError, data } = useTaskCategoriesQuery()

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const normalCategories = data.filter((c) => c.type == taskCategoryType.REGULAR)
  const prCategories = data.filter((c) => c.type == taskCategoryType.PROFILE_REQUIRED)

  const required = prCategories.length > 0 && (
    <>
      <Heading as="h1" variant="main-title">
        {component.profileRequiredTitle}
      </Heading>
      {component.profileRequiredMessage && <Markdown text={component.profileRequiredMessage} />}
      <VStack spacing={4} mt={5} align="stretch">
        {prCategories.map((category) => (
          <TaskCategoryListItem key={category.categoryId} category={category} />
        ))}
      </VStack>
    </>
  )

  return (
    <CmschPage loginRequired>
      <Helmet title={component.title} />
      {required}
      <Heading as="h1" variant="main-title">
        {component.regularTitle}
      </Heading>
      {component.regularMessage && <Markdown text={component.regularMessage} />}
      {normalCategories.length > 0 ? (
        <VStack spacing={4} mt={5} align="stretch">
          {normalCategories.map((category) => (
            <TaskCategoryListItem key={category.categoryId} category={category} />
          ))}
        </VStack>
      ) : (
        <Text>Nincs egyetlen feladat sem.</Text>
      )}
    </CmschPage>
  )
}

export default TaskCategoryListPage
