import { Badge, Box, Flex, Heading, Text, useColorModeValue, useToast, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { TaskStatusBadge } from './components/TaskStatusBadge'
import { useTasksInCategoryQuery } from '../../api/hooks/useTasksInCategoryQuery'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { AbsolutePaths } from '../../util/paths'
import { l } from '../../util/language'
import { LoadingPage } from '../loading/loading.page'

const TaskCategoryPage = () => {
  const { id } = useParams()
  const bg = useColorModeValue('brand.100', 'brand.500')
  const hoverBg = useColorModeValue('brand.200', 'brand.400')
  const toast = useToast()
  const navigate = useNavigate()
  const tasksQuery = useTasksInCategoryQuery(id, () => {
    navigate(AbsolutePaths.TASKS)
    toast({
      title: l('task-category-failed'),
      status: 'error',
      isClosable: true
    })
  })

  const taskConfig = useConfigContext()?.components.task

  if (!tasksQuery.isSuccess) return <LoadingPage />
  const category = tasksQuery.data
  const breadcrumbItems = [
    {
      title: taskConfig?.title || 'Feladatok',
      to: AbsolutePaths.TASKS
    },
    {
      title: category.categoryName
    }
  ]

  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title={category.categoryName} />
      <CustomBreadcrumb items={breadcrumbItems} />
      <Heading>{category.categoryName}</Heading>
      {category.tasks && category.tasks.length > 0 ? (
        <VStack spacing={4} mt={5} align="stretch">
          {category.tasks.map((task) => (
            <Box key={task.task.id} bg={bg} px={6} py={2} borderRadius="md" _hover={{ bgColor: hoverBg }}>
              <Link to={`${AbsolutePaths.TASKS}/${task.task.id}`}>
                <Flex align="center" justifyContent="space-between">
                  <Flex align="center">
                    <Text fontWeight="bold" fontSize="xl">
                      {task.task.title}
                    </Text>
                    {task.task.availableTo < new Date().valueOf() / 1000 && (
                      <Badge ml={5} variant="solid" colorScheme="red" fontSize="sm">
                        LEJÁRT
                      </Badge>
                    )}
                  </Flex>
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
}

export default TaskCategoryPage
