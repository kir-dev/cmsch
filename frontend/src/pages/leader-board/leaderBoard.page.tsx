import { CmschPage } from '../../common-components/layout/CmschPage'
import { Box, Divider, Flex, GridItem, Hide, HStack, Link, SimpleGrid, useColorModeValue, VStack, Text } from '@chakra-ui/react'
import { LeaderBoardListItem } from './components/LeaderBoardListIem'
import { ArrowDownIcon, ArrowRightIcon, ChevronDownIcon, ChevronUpIcon } from '@chakra-ui/icons'
import { useLeaderBoardQuery } from '../../api/hooks/useLeaderBoardQuery'
import { LeaderBoardView } from '../../util/views/leaderBoardView'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { Helmet } from 'react-helmet-async'

const LeaderboardPage = () => {
  const { data } = useLeaderBoardQuery(() => console.log('Event query failed!'))

  const userBg = useColorModeValue('purple.400', 'purple.400')
  const userHoverBg = useColorModeValue('purple.300', 'purple.300')

  const userBgDark = useColorModeValue('purple.600', 'purple.600')
  const userHoverBgDark = useColorModeValue('purple.400', 'purple.400')

  const groupBg = useColorModeValue('pink.400', 'pink.400')
  const groupHoverBg = useColorModeValue('pink.300', 'pink.300')

  const groupBgDark = useColorModeValue('pink.600', 'pink.600')
  const groupHoverBgDark = useColorModeValue('pink.400', 'pink.400')

  function gridColCount() {
    let count = 0
    if (data?.userScore) {
      count++
    }
    if (data?.groupScore) {
      count++
    }

    return count
  }

  const leadboardConfig = useConfigContext()?.components.leaderboard
  const title = leadboardConfig?.title || 'Toplista'
  return (
    <CmschPage>
      <Helmet title={title} />
      <SimpleGrid columns={{ sm: 1, md: gridColCount() }} spacing={5}>
        {data?.userScore !== undefined && (
          <Link href="#user_scores">
            <Box bg={userBgDark} px={6} py={2} borderRadius="md" _hover={{ bgColor: userHoverBgDark }}>
              <Flex align="center" justifyContent="space-between">
                <Text fontWeight="bold" fontSize="xl">
                  Saját pont:
                </Text>
                <Text fontWeight="bold" fontSize="xl" decoration="underline" mx={5}>
                  {data.userScore}
                </Text>
                <ArrowRightIcon></ArrowRightIcon>
              </Flex>
            </Box>
          </Link>
        )}
        {data?.groupScore !== undefined && (
          <Link href="#group_scores">
            <Box bg={groupBgDark} px={6} py={2} borderRadius="md" _hover={{ bgColor: groupHoverBgDark }}>
              <Flex align="center" justifyContent="space-between">
                <Text fontWeight="bold" fontSize="xl">
                  Csapat pont:
                </Text>
                <Text fontWeight="bold" fontSize="xl" decoration="underline" mx={5}>
                  {data?.groupScore}
                </Text>
                <ArrowRightIcon></ArrowRightIcon>
              </Flex>
            </Box>
          </Link>
        )}
        <GridItem colSpan={{ sm: 1, md: 2 }}>
          <Divider bg="gray.400" height={2}></Divider>
        </GridItem>

        {data?.userBoard && data?.userBoard?.length > 0 && (
          <VStack align="stretch" spacing={5}>
            <Box id="user_scores" bg={userBgDark} px={6} py={4} borderRadius="md" _hover={{ bgColor: userHoverBgDark }}>
              <Flex align="center" justifyContent="space-between">
                <Text fontWeight="bold" fontSize="xl">
                  Egyéni toplista
                </Text>
                <ChevronDownIcon />
              </Flex>
            </Box>

            {data.userBoard.map((item) => (
              <Box bg={userBg} px={6} py={2} borderRadius="md" _hover={{ bgColor: userHoverBg }}>
                <Flex align="center" justifyContent="space-between">
                  <Text fontWeight="bold" fontSize="xl">
                    {item.name}
                  </Text>
                  <Text fontWeight="bold" fontSize="xl">
                    {item.score}
                  </Text>
                </Flex>
              </Box>
            ))}
          </VStack>
        )}

        {data?.groupBoard && data.groupBoard.length > 0 && (
          <VStack align="stretch" spacing={5}>
            <Box id="group_scores" bg={groupBgDark} px={6} py={4} borderRadius="md" _hover={{ bgColor: groupHoverBgDark }}>
              <Flex align="center" justifyContent="space-between">
                <Text fontWeight="bold" fontSize="xl">
                  Csapat toplista
                </Text>
                <ChevronDownIcon />
              </Flex>
            </Box>

            {data?.groupBoard.map((item) => (
              <Box bg={groupBg} px={6} py={2} borderRadius="md" _hover={{ bgColor: groupHoverBg }}>
                <Flex align="center" justifyContent="space-between">
                  <Text fontWeight="bold" fontSize="xl">
                    {item.name}
                  </Text>
                  <Text fontWeight="bold" fontSize="xl">
                    {item.score}
                  </Text>
                </Flex>
              </Box>
            ))}
          </VStack>
        )}
      </SimpleGrid>
    </CmschPage>
  )
}

export default LeaderboardPage
