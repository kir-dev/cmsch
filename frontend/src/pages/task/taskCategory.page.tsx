import { Box, Flex, Heading, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { Link, useParams } from 'react-router-dom'
import { Loading } from '../../common-components/Loading'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { TaskStatusBadge } from './components/TaskStatusBadge'
import { TaskSkeleton } from './components/TaskListSkeleton'
import { useTasksInCategoryQuery } from '../../api/hooks/useTasksInCategoryQuery'

const TaskCategoryPage = () => {
  const { id } = useParams()

  const tasksQuery = useTasksInCategoryQuery(id, () => {
    console.error('Nem sikerült lekérdezni a Bucketlist feladatokat ehhez a kategóriához.')
  })

  if (tasksQuery.isSuccess) {
    const category = tasksQuery.data
    const breadcrumbItems = [
      {
        title: 'Bucketlist',
        to: '/bucketlist'
      },
      {
        title: category.categoryName
      }
    ]

    return (
      <CmschPage loginRequired groupRequired>
        <Helmet title={category.categoryName} />
        <CustomBreadcrumb items={breadcrumbItems} />
        <Heading>Bucketlist kategória: {category.categoryName}</Heading>
        {category.tasks && category.tasks.length > 0 ? (
          <VStack spacing={4} mt={5} align="stretch">
            {category.tasks.map((ach) => (
              <Box
                key={ach.task.id}
                bg={useColorModeValue('gray.200', 'gray.600')}
                px={6}
                py={2}
                borderRadius="md"
                _hover={{ bgColor: useColorModeValue('brand.300', 'brand.700') }}
              >
                <Link to={`/bucketlist/${ach.task.id}`}>
                  <Flex align="center" justifyContent="space-between">
                    <Text fontWeight="bold" fontSize="xl">
                      {ach.task.title}
                    </Text>
                    <TaskStatusBadge status={ach.status} fontSize="sm" />
                  </Flex>
                </Link>
              </Box>
            ))}
          </VStack>
        ) : (
          <Text>Nincs egyetlen bucketlist feladat se ebben a kategóriában.</Text>
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
