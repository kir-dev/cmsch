import { Heading, Text, useToast, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useNavigate } from 'react-router-dom'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { useTaskCategoriesQuery } from '../../api/hooks/useTaskCategoriesQuery'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { taskCategoryType } from '../../util/views/task.view'
import { TaskCategoryListItem } from './components/TaskCategoryListIem'
import Markdown from '../../common-components/Markdown'
import { l } from '../../util/language'
import { LoadingPage } from '../loading/loading.page'

const TaskCategoryList = () => {
  const taskConfig = useConfigContext()?.components.task

  const toast = useToast()
  const navigate = useNavigate()
  const categoriesQuery = useTaskCategoriesQuery(() => {
    navigate('/')
    toast({
      title: l('task-list-failed'),
      status: 'error',
      isClosable: true
    })
  })

  if (!categoriesQuery.isSuccess) return <LoadingPage />
  const normalCategories = categoriesQuery.data.filter((c) => c.type == taskCategoryType.REGULAR)
  const prCategories = categoriesQuery.data.filter((c) => c.type == taskCategoryType.PROFILE_REQUIRED)

  const required = prCategories.length > 0 && (
    <>
      <Heading>{taskConfig?.profileRequiredTitle}</Heading>
      {taskConfig?.profileRequiredMessage && <Markdown text={taskConfig?.profileRequiredMessage} />}
      <VStack spacing={4} mt={5} align="stretch">
        {prCategories.map((category) => (
          <TaskCategoryListItem key={category.categoryId} category={category} />
        ))}
      </VStack>
    </>
  )
  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title={taskConfig?.title} />
      {required}
      <Heading>{taskConfig?.regularTitle}</Heading>
      {taskConfig?.regularMessage && <Markdown text={taskConfig?.regularMessage} />}
      {normalCategories.length > 0 ? (
        <VStack spacing={4} mt={5} align="stretch">
          {normalCategories.map((category) => (
            <TaskCategoryListItem key={category.categoryId} category={category} />
          ))}
        </VStack>
      ) : (
        <Text>Nincs egyetlen feladat se.</Text>
      )}
    </CmschPage>
  )
}

export default TaskCategoryList
