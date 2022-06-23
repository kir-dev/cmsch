import { Box, Flex, Heading, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import axios from 'axios'
import { useEffect, useState } from 'react'
import { Helmet } from 'react-helmet'
import { Link } from 'react-router-dom'
import { AllTaskCategories, TaskCategoryPreview } from '../../util/views/task.view'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { TaskSkeleton } from './components/TaskListSkeleton'
import { progress, progressGradient } from './util/taskCategoryProgress'

const TaskCategoryList = () => {
  const bg = useColorModeValue('gray.200', 'gray.600')
  const [categories, setCategories] = useState<TaskCategoryPreview[]>([])
  const [loading, setLoading] = useState<boolean>(true)

  useEffect(() => {
    axios
      .get<AllTaskCategories>(`/api/task`)
      .then((res) => {
        setCategories(res.data.categories)
        setLoading(false)
      })
      .catch(() => {
        console.error('Nem sikerült lekérdezni a Bucketlist feladatokat.')
      })
  }, [])

  if (loading)
    return (
      <Loading>
        <TaskSkeleton height="4rem" title="Bucketlist" />
      </Loading>
    )
  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title="Bucketlist kategóriák" />
      <Heading>Bucketlist</Heading>
      {categories.length > 0 ? (
        <VStack spacing={4} mt={5} align="stretch">
          {categories.map((category) => (
            <Box
              key={category.categoryId}
              bg={bg}
              px={6}
              py={2}
              borderRadius="md"
              _hover={{ bgColor: useColorModeValue('brand.300', 'brand.700') }}
            >
              <Link to={`/bucketlist/kategoria/${category.categoryId}`}>
                <Flex align="center" justifyContent="space-between">
                  <Text fontWeight="bold" fontSize="xl">
                    {category.name}
                  </Text>
                  <Box
                    bgGradient={progressGradient(progress(category), useColorModeValue('brand.500', 'brand.600'))}
                    px={1}
                    py={1}
                    borderRadius="6px"
                  >
                    <Text bg={bg} px={4} py={2} borderRadius="6px" fontWeight="bold">
                      {category.approved + category.notGraded} / {category.sum}
                    </Text>
                  </Box>
                </Flex>
              </Link>
            </Box>
          ))}
        </VStack>
      ) : (
        <Text>Nincs egyetlen bucketlist feladat se.</Text>
      )}
    </CmschPage>
  )
}

export default TaskCategoryList
