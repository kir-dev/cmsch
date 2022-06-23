import { Box, Flex, Heading, Skeleton, Stack, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import axios from 'axios'
import { FC, useEffect, useState } from 'react'
import { Helmet } from 'react-helmet'
import { Link, useParams } from 'react-router-dom'
import { TaskCategoryFullDetails } from '../../util/views/task.view'
import { Loading } from '../../common-components/Loading'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { TaskStatusBadge } from './components/TaskStatusBadge'
import { TaskSkeleton } from './components/TaskListSkeleton'

const TaskCategoryPage = () => {
  const [category, setCategory] = useState<TaskCategoryFullDetails | undefined>(undefined)
  const { id } = useParams()

  useEffect(() => {
    axios
      .get<TaskCategoryFullDetails>(`/api/task/category/${id}`)
      .then((res) => {
        setCategory(res.data)
      })
      .catch(() => {
        console.error('Nem sikerült lekérdezni a Bucketlist feladatokat ebben a kategóriában.')
      })
  }, [])

  if (!category) {
    return (
      <Loading>
        <TaskSkeleton height="3rem" />
      </Loading>
    )
  }

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
}

export default TaskCategoryPage
