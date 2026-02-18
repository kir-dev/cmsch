import { Box, Heading, HStack, Tab, TabList, TabPanel, TabPanels, Tabs } from '@chakra-ui/react'
import { useMatch, useNavigate } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useLeaderBoardQuery } from '../../api/hooks/leaderboard/useLeaderBoardQuery'
import { BoardStat } from '../../common-components/BoardStat'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown.tsx'
import { PageStatus } from '../../common-components/PageStatus'
import { useBrandColor } from '../../util/core-functions.util.ts'
import LeaderboardByCategoryPage from './leaderboardByCategory.page.tsx'
import LeaderboardByUserOrGroupPage from './leaderboardByUserOrGroup.page.tsx'

const LeaderboardPage = () => {
  const component = useConfigContext()?.components?.leaderboard
  const { data, isError, isLoading } = useLeaderBoardQuery(component?.leaderboardDetailsEnabled ? 'detailed' : 'short')
  const byCategory = useMatch('/leaderboard/category')
  const navigate = useNavigate()
  const brandColor = useBrandColor()

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  const tabIndex = byCategory ? 1 : 0

  const onTabSelected = (i: number) => {
    switch (i) {
      case 0:
        navigate('/leaderboard')
        break
      case 1:
        navigate('/leaderboard/category')
        break
    }
  }

  return (
    <CmschPage disablePadding={true} title={title}>
      <Heading as="h1" variant="main-title" textAlign="center" m="2rem">
        {title}
      </Heading>
      {component.topMessage ? (
        <Box textAlign="center">
          <Markdown text={component.topMessage} />
        </Box>
      ) : (
        <></>
      )}
      <HStack my={5}>
        {data?.userScore !== undefined && <BoardStat label="Saját pont" value={data.userScore} />}
        {data?.groupScore !== undefined && <BoardStat label={`${component.myGroupName} pontjai`} value={data.groupScore} />}
      </HStack>
      <Tabs isLazy isFitted colorScheme={brandColor} variant="enclosed" index={tabIndex} onChange={onTabSelected}>
        <TabList>
          <Tab>{component.groupBoardName}</Tab>
          {component.leaderboardDetailsByCategoryEnabled && <Tab>{component.leaderBoardCategoryName}</Tab>}
        </TabList>
        <TabPanels>
          <TabPanel px={0}>
            <LeaderboardByUserOrGroupPage />
          </TabPanel>
          <TabPanel px={0}>
            <LeaderboardByCategoryPage />
          </TabPanel>
        </TabPanels>
      </Tabs>
    </CmschPage>
  )
}

export default LeaderboardPage
