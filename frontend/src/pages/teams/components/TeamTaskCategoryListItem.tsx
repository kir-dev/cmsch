import { Box, CircularProgress, Flex, HStack, Text } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { joinPath, useOpaqueBackground } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'
import { TeamTaskCategoriesView } from '../../../util/views/team.view'

export const TeamTaskCategoryListItem = ({ category }: { category: TeamTaskCategoriesView }) => {
  const bg = useOpaqueBackground(1)
  const hoverBg = useOpaqueBackground(2)
  const innerComponent = (
    <Flex align="center" justifyContent="space-between">
      <Text fontWeight="bold" fontSize="xl">
        {category.name}
      </Text>
      <HStack>
        <Text fontWeight="bold">
          {category.completed}/{category.outOf}
        </Text>
        <CircularProgress size={10} value={category.completed} max={category.outOf} color={'green.400'} />
      </HStack>
    </Flex>
  )
  if (typeof category.navigate === 'undefined' || category.navigate === null)
    return (
      <Box bg={bg} px={6} py={2} marginTop={5} borderRadius="md">
        {innerComponent}
      </Box>
    )
  return (
    <Box bg={bg} px={6} py={2} marginTop={5} borderRadius="md" _hover={{ bgColor: hoverBg }}>
      <Link to={joinPath(AbsolutePaths.TASKS, 'category', category.navigate)}>{innerComponent}</Link>
    </Box>
  )
}
