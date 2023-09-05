import { Box, CircularProgress, Flex, HStack, Text, useColorModeValue } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { AbsolutePaths } from '../../../util/paths'
import { TaskCategoryPreview } from '../../../util/views/task.view'

export const TaskCategoryListItem = ({ category }: { category: TaskCategoryPreview }) => {
  const bg = useColorModeValue('#00000020', '#FFFFFF20')
  const hoverBg = useColorModeValue('#00000030', '#FFFFFF30')
  return (
    <Box bg={bg} px={6} py={2} borderRadius="md" _hover={{ bgColor: hoverBg }}>
      <Link to={`${AbsolutePaths.TASKS}/category/${category.categoryId}`}>
        <Flex align="center" justifyContent="space-between">
          <Text fontWeight="bold" fontSize="xl">
            {category.name}
          </Text>
          <HStack>
            <Text fontWeight="bold">
              {category.approved + category.notGraded}/{category.sum}
            </Text>
            <CircularProgress
              size={10}
              value={category.approved + category.notGraded}
              max={category.sum}
              color={category.notGraded > 0 ? 'yellow.400' : 'green.400'}
            />
          </HStack>
        </Flex>
      </Link>
    </Box>
  )
}
