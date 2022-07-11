import { Box, Flex, Text, useColorModeValue } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { AbsolutePaths } from '../../../util/paths'
import { TaskCategoryPreview } from '../../../util/views/task.view'
import { progressGradient, progress } from '../util/taskCategoryProgress'

export const TaskCategoryListItem = ({ category }: { category: TaskCategoryPreview }) => {
  const bg = useColorModeValue('gray.200', 'gray.600')
  const hoverBg = useColorModeValue('brand.300', 'brand.700')
  const gradientBg = useColorModeValue('brand.500', 'brand.600')
  return (
    <Box bg={bg} px={6} py={2} borderRadius="md" _hover={{ bgColor: hoverBg }}>
      <Link to={`${AbsolutePaths.TASKS}/category/${category.categoryId}`}>
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
  )
}
