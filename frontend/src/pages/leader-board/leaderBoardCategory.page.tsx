import { Helmet } from 'react-helmet-async'
import {
  Divider,
  Flex,
  Heading,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  Text,
  useBreakpoint,
  useBreakpointValue,
  VStack
} from '@chakra-ui/react'
import { CustomTab } from '../events/components/CustomTab'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { l } from '../../util/language'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { Navigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { Loading } from '../../common-components/Loading'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { useLeaderBoardQuery } from '../../api/hooks/useLeaderBoardQuery'
import { LinkButton } from '../../common-components/LinkButton'

export default function LeaderboardCategoryPage() {
  const { data, isLoading, isError, error } = useLeaderBoardQuery('categorized')
  const leaderboardConfig = useConfigContext()?.components.leaderboard

  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  const { sendMessage } = useServiceContext()

  const title = leaderboardConfig?.title || 'Toplista'

  if (isError) {
    sendMessage(l('result-query-failed') + error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (isLoading) {
    return <Loading />
  }

  if (!leaderboardConfig?.leaderboardDetailsByCategoryEnabled) return <Navigate to={AbsolutePaths.LEADER_BOARD} />

  const userBoard = leaderboardConfig?.showUserBoard && (
    <LeaderBoardTable
      data={data?.userBoard || []}
      showGroup={leaderboardConfig?.showGroupOfUser}
      categorized
      detailed={leaderboardConfig?.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )
  const groupBoard = leaderboardConfig?.showGroupBoard && (
    <LeaderBoardTable data={data?.groupBoard || []} detailed={leaderboardConfig?.leaderboardDetailsEnabled} suffix="pont" categorized />
  )
  return (
    <CmschPage>
      <Helmet title={title} />
      <Flex wrap="wrap" justify="space-between">
        <VStack mb={5} align="flex-start">
          <Heading>{title}</Heading>
          <Text>Kategóriák szerint</Text>
        </VStack>
        <VStack>
          <LinkButton href={AbsolutePaths.LEADER_BOARD} my={5}>
            Összesített nézet
          </LinkButton>
        </VStack>
      </Flex>
      <Divider mb={10} />

      {leaderboardConfig?.showUserBoard && leaderboardConfig?.showGroupBoard ? (
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
