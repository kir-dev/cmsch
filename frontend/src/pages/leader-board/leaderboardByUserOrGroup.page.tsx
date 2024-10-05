import { TabList, TabPanel, TabPanels, Tabs, useBreakpointValue } from '@chakra-ui/react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useLeaderBoardQuery } from '../../api/hooks/leaderboard/useLeaderBoardQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CustomTabButton } from '../../common-components/CustomTabButton'

import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { PageStatus } from '../../common-components/PageStatus'

const LeaderboardByUserOrGroupPage = () => {
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const component = useConfigContext()?.components.leaderboard
  const { data, isError, isLoading } = useLeaderBoardQuery(component?.leaderboardDetailsEnabled ? 'detailed' : 'short')

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  const userBoard = component.showUserBoard && (
    <LeaderBoardTable
      searchEnabled={component.searchEnabled}
      data={data?.userBoard || []}
      showGroup={component.showGroupOfUser}
      detailed={component.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )
  const groupBoard = component.showGroupBoard && (
    <LeaderBoardTable
      searchEnabled={component.searchEnabled}
      data={data?.groupBoard || []}
      detailed={component.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )

  return (
    <>
      {component.showUserBoard && component.showGroupBoard ? (
        <Tabs size={tabsSize} variant="soft-rounded" colorScheme="brand" color="brandForeground">
          <TabList px="2rem">
            {data?.userBoard && <CustomTabButton>Egyéni</CustomTabButton>}
            {data?.groupBoard && <CustomTabButton>Csoportos</CustomTabButton>}
          </TabList>
          <TabPanels>
            {data?.userBoard && <TabPanel px={0}>{userBoard}</TabPanel>}
            {data?.groupBoard && <TabPanel px={0}>{groupBoard}</TabPanel>}
          </TabPanels>
        </Tabs>
      ) : (
        <>
          {data?.userBoard && userBoard}
          {data?.groupBoard && groupBoard}
        </>
      )}
    </>
  )
}

export default LeaderboardByUserOrGroupPage
