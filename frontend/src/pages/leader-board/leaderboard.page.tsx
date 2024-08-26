import { Heading, HStack, Tab, TabList, TabPanel, TabPanels, Tabs } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useLeaderBoardQuery } from '../../api/hooks/leaderboard/useLeaderBoardQuery'
import { BoardStat } from '../../common-components/BoardStat'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import LeaderboardByCategoryPage from './leaderboardByCategory.page.tsx'
import LeaderboardByUserOrGroupPage from './leaderboardByUserOrGroup.page.tsx'
import { useMatch, useNavigate } from 'react-router-dom'

const LeaderboardPage = () => {
  const component = useConfigContext()?.components.leaderboard
  const { data, isError, isLoading } = useLeaderBoardQuery(component?.leaderboardDetailsEnabled ? 'detailed' : 'short')
  const byCategory = useMatch('/leaderboard/category')
  const byToken = useMatch('/leaderboard/token')
  const navigate = useNavigate()

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  const tabIndex = byToken ? 2 : byCategory ? 1 : 0

  const handleTabChange = (i: number) => {
    switch (i) {
      case 0:
        navigate('/leaderboard')
        break
      case 1:
        navigate('/leaderboard/category')
        break
      case 2:
        navigate('/leaderboard/token')
        break
    }
  }

  return (
    <CmschPage disablePadding={true}>
      <Helmet title={title} />
      <Heading as="h1" variant="main-title" textAlign="center" m="2rem">
        {title}
      </Heading>

      <HStack my={5}>
        {data?.userScore !== undefined && <BoardStat label="Saját pont" value={data.userScore} />}
        {data?.groupScore !== undefined && <BoardStat label="Csapat pont" value={data.groupScore} />}
      </HStack>

      <Tabs isLazy isFitted colorScheme="brand" variant="enclosed" index={tabIndex} onChange={handleTabChange}>
        <TabList>
          <Tab>Csapat</Tab>
          <Tab>Kategória</Tab>
          <Tab>QR kód</Tab>
        </TabList>
        <TabPanels>
          <TabPanel px={0}>
            <LeaderboardByUserOrGroupPage />
          </TabPanel>
          <TabPanel px={0}>
            <LeaderboardByCategoryPage />
          </TabPanel>
          <TabPanel px={0}>Hello</TabPanel>
        </TabPanels>
      </Tabs>
    </CmschPage>
  )
}

export default LeaderboardPage