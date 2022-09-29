import {
  Divider,
  Flex,
  Heading,
  HStack,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  useBreakpoint,
  useBreakpointValue,
  useToast,
  VStack
} from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { useLeaderBoardQuery } from '../../api/hooks/useLeaderBoardQuery'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { BoardStat } from '../../common-components/BoardStat'
import { CustomTab } from '../events/components/CustomTab'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { Navigate } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { AbsolutePaths } from '../../util/paths'
import { Loading } from '../../common-components/Loading'
import { l } from '../../util/language'
import { LinkButton } from '../../common-components/LinkButton'

const LeaderboardPage = () => {
  const leaderboardConfig = useConfigContext()?.components.leaderboard
  const toast = useToast()
  const onQueryFail = () => toast({ title: l('result-query-failed'), status: 'error' })
  const leaderboardQuery = useLeaderBoardQuery(leaderboardConfig?.leaderboardDetailsEnabled ? 'detailed' : 'short', onQueryFail)

  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  const { sendMessage } = useServiceContext()

  const title = leaderboardConfig?.title || 'Toplista'

  if (leaderboardQuery.isError) {
    sendMessage(l('result-query-failed') + leaderboardQuery.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (leaderboardQuery.isLoading) {
    return <Loading />
  }

  const userBoard = leaderboardConfig?.showUserBoard && (
    <LeaderBoardTable
      data={leaderboardQuery.data?.userBoard || []}
      showGroup={leaderboardConfig?.showGroupOfUser}
      detailed={leaderboardConfig?.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )
  const groupBoard = leaderboardConfig?.showGroupBoard && (
    <LeaderBoardTable
      data={leaderboardQuery.data?.groupBoard || []}
      detailed={leaderboardConfig?.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )

  return (
    <CmschPage>
      <Helmet title={title} />
      <Flex wrap="wrap" justify="space-between">
        <VStack>
          <Heading>{title}</Heading>
        </VStack>
        {leaderboardConfig?.leaderboardDetailsByCategoryEnabled && (
          <VStack>
            <LinkButton href={AbsolutePaths.LEADER_BOARD + '/category'} my={5}>
              Kategóriák nézet
            </LinkButton>
          </VStack>
        )}
      </Flex>
      <HStack my={5}>
        {leaderboardQuery.data?.userScore !== undefined && <BoardStat label="Saját pont" value={leaderboardQuery.data.userScore} />}
        {leaderboardQuery.data?.groupScore !== undefined && <BoardStat label="Csapat pont" value={leaderboardQuery.data.groupScore} />}
      </HStack>
      <Divider mb={10} />

      {leaderboardConfig?.showUserBoard && leaderboardConfig?.showGroupBoard ? (
        <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="unstyled">
          <TabList>
            {leaderboardQuery.data?.userBoard && <CustomTab>Egyéni</CustomTab>}
            {leaderboardQuery.data?.groupBoard && <CustomTab>Csoportos</CustomTab>}
          </TabList>
          <TabPanels>
            {leaderboardQuery.data?.userBoard && <TabPanel>{userBoard}</TabPanel>}

            {leaderboardQuery.data?.groupBoard && <TabPanel>{groupBoard}</TabPanel>}
          </TabPanels>
        </Tabs>
      ) : (
        <>
          {leaderboardQuery.data?.userBoard && userBoard}
          {leaderboardQuery.data?.groupBoard && groupBoard}
        </>
      )}
    </CmschPage>
  )
}

export default LeaderboardPage
