import { Box, Flex, Heading, Text, useColorModeValue, useToast, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { Loading } from '../../common-components/Loading'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { TaskStatusBadge } from './components/TaskStatusBadge'
import { TaskSkeleton } from './components/TaskListSkeleton'
import { useTasksInCategoryQuery } from '../../api/hooks/useTasksInCategoryQuery'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

const TaskCategoryPage = () => {
  const { id } = useParams()
  const bg = useColorModeValue('gray.200', 'gray.600')
  const hoverBg = useColorModeValue('brand.300', 'brand.700')
  const toast = useToast()
  const navigate = useNavigate()
  const tasksQuery = useTasksInCategoryQuery(id, () => {
    navigate('/tasks')
    toast({
      title: 'Nem sikerült lekérni ezt a feladat kategóriát',
      status: 'error',
      isClosable: true
    })
  })

  const taskConfig = useConfigContext()?.components.task

  if (tasksQuery.isSuccess) {
    const category = tasksQuery.data
    const breadcrumbItems = [
      {
        title: taskConfig?.title || 'Bucketlist',
        to: '/tasks'
      },
      {
        title: category.categoryName
      }
    ]

    return (
      <CmschPage loginRequired groupRequired>
        <Helmet title={category.categoryName} />
        <CustomBreadcrumb items={breadcrumbItems} />
        <Heading>{`${taskConfig?.title} kategória: ${category.categoryName}`}</Heading>
        {category.tasks && category.tasks.length > 0 ? (
          <VStack spacing={4} mt={5} align="stretch">
            {category.tasks.map((task) => (
              <Box key={task.task.id} bg={bg} px={6} py={2} borderRadius="md" _hover={{ bgColor: hoverBg }}>
                <Link to={`/tasks/${task.task.id}`}>
                  <Flex align="center" justifyContent="space-between">
                    <Text fontWeight="bold" fontSize="xl">
                      {task.task.title}
                    </Text>
                    <TaskStatusBadge status={task.status} fontSize="sm" />
                  </Flex>
                </Link>
              </Box>
            ))}
          </VStack>
        ) : (
          <Text>Nincs egyetlen feladat se ebben a kategóriában.</Text>
        )}
      </CmschPage>
    )
  } else {
    return (
      <Loading>
        <TaskSkeleton height="3rem" />
      </Loading>
    )
  }
}

export default TaskCategoryPage
