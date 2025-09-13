import { Box, Text } from '@chakra-ui/react'
import { useOpaqueBackground } from '../../../util/core-functions.util.ts'
import { Riddle } from '../../../util/views/riddle.view.ts'

interface RiddleListItemProps {
  riddle: Riddle
  onClick: () => void
}

export function RiddleListItem({ riddle, onClick }: RiddleListItemProps) {
  const bg = useOpaqueBackground(1)
  const hoverBg = useOpaqueBackground(2)

  return (
    <Box key={riddle.id} bg={bg} px={6} py={2} borderRadius="md" cursor="pointer" _hover={{ bgColor: hoverBg }} onClick={onClick}>
      <Text fontWeight="bold" fontSize="xl">
        {riddle.title}
      </Text>
    </Box>
  )
}
