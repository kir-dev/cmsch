import { Divider, Heading, Text, useToast, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { useNavigate } from 'react-router-dom'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { TaskSkeleton } from './components/TaskListSkeleton'
import { useTaskCategoriesQuery } from '../../api/hooks/useTaskCategoriesQuery'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { taskCategoryType } from '../../util/views/task.view'
import { TaskCategoryListItem } from './components/TaskCategoryListIem'
import { Paragraph } from '../../common-components/Paragraph'

const TaskCategoryList = () => {
  const taskConfig = useConfigContext()?.components.task

  const toast = useToast()
  const navigate = useNavigate()
  const categoriesQuery = useTaskCategoriesQuery(() => {
    navigate('/')
    toast({
      title: 'Nem sikerült lekérni a feladatokat',
      status: 'error',
      isClosable: true
    })
  })

  if (categoriesQuery.isSuccess) {
    const normalCategories = categoriesQuery.data.filter((c) => c.type == taskCategoryType.REGULAR)
    const prCategories = categoriesQuery.data.filter((c) => c.type == taskCategoryType.PROFILE_REQUIRED)

    const required = prCategories.length > 0 && (
      <>
        <Heading>{taskConfig?.profileRequiredTitle}</Heading>
        {taskConfig?.profileRequiredMessage && <Paragraph>{taskConfig?.profileRequiredMessage}</Paragraph>}
        <VStack spacing={4} mt={5} align="stretch">
          {prCategories.map((category) => (
            <TaskCategoryListItem key={category.categoryId} category={category} />
          ))}
        </VStack>
        <Divider />
      </>
    )
    return (
      <CmschPage loginRequired groupRequired>
        <Helmet title={taskConfig?.title} />
        {required}
        <Heading>{taskConfig?.regularTitle}</Heading>
        {taskConfig?.regularMessage && <Paragraph>{taskConfig?.regularMessage}</Paragraph>}
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
  } else {
    return (
      <Loading>
        <TaskSkeleton height="4rem" title={taskConfig?.title} />
      </Loading>
    )
  }
}

export default TaskCategoryList
