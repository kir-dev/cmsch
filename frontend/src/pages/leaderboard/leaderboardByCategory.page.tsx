import { TabList, TabPanel, TabPanels, Tabs } from '@chakra-ui/react'
import { Navigate } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useLeaderBoardQuery } from '../../api/hooks/leaderboard/useLeaderBoardQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CustomTabButton } from '../../common-components/CustomTabButton'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { PageStatus } from '../../common-components/PageStatus'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../util/paths'

export default function LeaderboardByCategoryPage() {
  const { data, isLoading, isError } = useLeaderBoardQuery('categorized')
  const component = useConfigContext()?.components?.leaderboard
  const brandColor = useBrandColor()

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  if (!component.leaderboardDetailsByCategoryEnabled) return <Navigate to={AbsolutePaths.LEADER_BOARD} />

  const userBoard = component.showUserBoard && (
    <LeaderBoardTable
      searchEnabled={component.searchEnabled}
      data={data?.userBoard || []}
      showGroup={component.showGroupOfUser}
      categorized
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
      categorized
    />
  )
  return (
    <>
      {component.showUserBoard && component.showGroupBoard ? (
        <Tabs size={{ base: 'sm', md: 'md' }} variant="soft-rounded" colorScheme={brandColor}>
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
