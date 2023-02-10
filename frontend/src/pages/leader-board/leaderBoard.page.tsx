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
import { l } from '../../util/language'
import { LinkButton } from '../../common-components/LinkButton'
import { LoadingPage } from '../loading/loading.page'

const LeaderboardPage = () => {
  const toast = useToast()
  const component = useConfigContext()?.components.leaderboard
  const onQueryFail = () => toast({ title: l('result-query-failed'), status: 'error' })
  const { data, isError, isLoading, error } = useLeaderBoardQuery(component?.leaderboardDetailsEnabled ? 'detailed' : 'short', onQueryFail)

  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  const { sendMessage } = useServiceContext()

  if (!component) {
    sendMessage(l('component-unavailable'))
    return <Navigate to={AbsolutePaths.ERROR} />
  }

  const title = component.title || 'Toplista'

  if (isError) {
    sendMessage(l('result-query-failed') + error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (isLoading) {
    return <LoadingPage />
  }

  const userBoard = component.showUserBoard && (
    <LeaderBoardTable
      data={data?.userBoard || []}
      showGroup={component.showGroupOfUser}
      detailed={component.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )
  const groupBoard = component.showGroupBoard && (
    <LeaderBoardTable data={data?.groupBoard || []} detailed={component.leaderboardDetailsEnabled} suffix="pont" />
  )

  return (
    <CmschPage>
      <Helmet title={title} />
      <Flex wrap="wrap" justify="space-between">
        <VStack>
          <Heading>{title}</Heading>
        </VStack>
        {component.leaderboardDetailsByCategoryEnabled && (
          <VStack>
            <LinkButton href={AbsolutePaths.LEADER_BOARD + '/category'} my={5}>
              Kategóriák nézet
            </LinkButton>
          </VStack>
        )}
      </Flex>
      <HStack my={5}>
        {data?.userScore !== undefined && <BoardStat label="Saját pont" value={data.userScore} />}
        {data?.groupScore !== undefined && <BoardStat label="Csapat pont" value={data.groupScore} />}
      </HStack>
      <Divider mb={10} />

      {component.showUserBoard && component.showGroupBoard ? (
        <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="unstyled">
          <TabList>
            {data?.userBoard && <CustomTab>Egyéni</CustomTab>}
            {data?.groupBoard && <CustomTab>Csoportos</CustomTab>}
          </TabList>
          <TabPanels>
            {data?.userBoard && <TabPanel>{userBoard}</TabPanel>}

            {data?.groupBoard && <TabPanel>{groupBoard}</TabPanel>}
          </TabPanels>
        </Tabs>
      ) : (
        <>
          {data?.userBoard && userBoard}
          {data?.groupBoard && groupBoard}
        </>
      )}
    </CmschPage>
  )
}

export default LeaderboardPage
