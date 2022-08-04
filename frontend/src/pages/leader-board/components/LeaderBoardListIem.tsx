import { ArrowRightIcon } from '@chakra-ui/icons'
import { Box, Flex, Text, useColorModeValue } from '@chakra-ui/react'
import { LeaderBoardItemView } from '../../../util/views/leaderBoardView'

export const LeaderBoardListItem = ({ leaderBoardItem }: { leaderBoardItem: LeaderBoardItemView }) => {
  const bg = useColorModeValue('gray.200', 'whiteAlpha.200')
  const hoverBg = useColorModeValue('brand.300', 'whiteAlpha.300')
  return (
    <Box bg={bg} px={6} py={2} borderRadius="md" _hover={{ bgColor: hoverBg }}>
      <Flex align="center" justifyContent="space-between">
        <Text fontWeight="bold" fontSize="xl">
          {leaderBoardItem.name}
        </Text>
        <Text>{leaderBoardItem.score}</Text>
        <ArrowRightIcon></ArrowRightIcon>
      </Flex>
    </Box>
  )
}
