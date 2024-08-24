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

const LeaderboardPage = () => {
  const component = useConfigContext()?.components.leaderboard
  const { data, isError, isLoading } = useLeaderBoardQuery(component?.leaderboardDetailsEnabled ? 'detailed' : 'short')

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

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

      <Tabs isLazy isFitted colorScheme="brand" variant="enclosed">
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
