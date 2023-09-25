import { Box, CircularProgress, Flex, HStack, Text } from '@chakra-ui/react'
import { useOpaqueBackground } from '../../../util/core-functions.util'
import { RiddleCategory } from '../../../util/views/riddle.view'

interface RiddleCategoryListItemProps {
  category: RiddleCategory
  onClick: () => void
}

export function RiddleCategoryListItem({ category, onClick }: RiddleCategoryListItemProps) {
  const bg = useOpaqueBackground(1)
  const hoverBg = useOpaqueBackground(2)

  return (
    <Box key={category.categoryId} bg={bg} px={6} py={2} borderRadius="md" cursor="pointer" _hover={{ bgColor: hoverBg }} onClick={onClick}>
      <Flex align="center" justifyContent="space-between">
        <Text fontWeight="bold" fontSize="xl">
          {category.title}
        </Text>
        <HStack>
          <Text fontWeight="bold">
            {category.completed}/{category.total}
          </Text>
          <CircularProgress size={10} value={category.completed} max={category.total} color="green.400" />
        </HStack>
      </Flex>
    </Box>
  )
}
