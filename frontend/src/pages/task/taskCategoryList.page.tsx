import { Box, Flex, Heading, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { Link } from 'react-router-dom'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { TaskSkeleton } from './components/TaskListSkeleton'
import { progress, progressGradient } from './util/taskCategoryProgress'
import { useTaskCategoriesQuery } from '../../api/hooks/useTaskCategoriesQuery'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

const TaskCategoryList = () => {
  const taskConfig = useConfigContext()?.components.task
  const bg = useColorModeValue('gray.200', 'gray.600')
  const hoverBg = useColorModeValue('brand.300', 'brand.700')
  const gradientBg = useColorModeValue('brand.500', 'brand.600')
  const categoriesQuery = useTaskCategoriesQuery(() => {
    console.error('Nem sikerült lekérdezni a feladatokat.')
  })

  if (categoriesQuery.isSuccess) {
    const categories = categoriesQuery.data
    return (
      <CmschPage loginRequired groupRequired>
        <Helmet title={`${taskConfig?.title} kategóriák`} />
        <Heading>{`${taskConfig?.title} kategóriák`}</Heading>
        {categories.length > 0 ? (
          <VStack spacing={4} mt={5} align="stretch">
            {categories.map((category) => (
              <Box key={category.categoryId} bg={bg} px={6} py={2} borderRadius="md" _hover={{ bgColor: hoverBg }}>
                <Link to={`/bucketlist/kategoria/${category.categoryId}`}>
                  <Flex align="center" justifyContent="space-between">
                    <Text fontWeight="bold" fontSize="xl">
                      {category.name}
                    </Text>
                    <Box bgGradient={progressGradient(progress(category), gradientBg)} px={1} py={1} borderRadius="6px">
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
