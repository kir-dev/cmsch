import { Divider, Heading, HStack, TabList, TabPanel, TabPanels, Tabs, useBreakpoint, useBreakpointValue } from '@chakra-ui/react'
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

const LeaderboardPage = () => {
  const leaderboardQuery = useLeaderBoardQuery(() => console.log('Leaderboard query failed!'))

  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  const { sendMessage } = useServiceContext()

  const leadboardConfig = useConfigContext()?.components.leaderboard
  const title = leadboardConfig?.title || 'Toplista'

  if (leaderboardQuery.isError) {
    sendMessage(l('result-query-failed') + leaderboardQuery.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (leaderboardQuery.isLoading) {
    return <Loading />
  }

  const userBoard = <LeaderBoardTable data={leaderboardQuery.data?.userBoard || []} showGroup={leadboardConfig?.showGroupOfUser} />
  const groupBpard = <LeaderBoardTable data={leaderboardQuery.data?.groupBoard || []} />

  return (
    <CmschPage>
      <Helmet title={title} />
      <Heading>{title}</Heading>
      <HStack my={5}>
        {leaderboardQuery.data?.userScore !== undefined && <BoardStat label="Saját pont" value={leaderboardQuery.data.userScore} />}
        {leaderboardQuery.data?.groupScore !== undefined && <BoardStat label="Csapat pont" value={leaderboardQuery.data.groupScore} />}
      </HStack>
      <Divider mb={10} />

      {leaderboardQuery.data?.userBoard && leaderboardQuery.data?.groupBoard ? (
        <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="unstyled">
          <TabList>
            {leaderboardQuery.data?.userBoard && <CustomTab>Egyéni</CustomTab>}
            {leaderboardQuery.data?.groupBoard && <CustomTab>Csoportos</CustomTab>}
          </TabList>
          <TabPanels>
            {leaderboardQuery.data?.userBoard && <TabPanel>{userBoard}</TabPanel>}

            {leaderboardQuery.data?.groupBoard && <TabPanel>{groupBpard}</TabPanel>}
          </TabPanels>
        </Tabs>
      ) : (
        <>
          {leaderboardQuery.data?.userBoard && userBoard}
          {leaderboardQuery.data?.groupBoard && groupBpard}
        </>
      )}
    </CmschPage>
  )
}

export default LeaderboardPage
