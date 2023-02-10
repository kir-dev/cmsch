import { Helmet } from 'react-helmet-async'
import { Divider, Flex, Heading, TabList, TabPanel, TabPanels, Tabs, Text, useBreakpoint, VStack } from '@chakra-ui/react'
import { CustomTab } from '../events/components/CustomTab'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { l } from '../../util/language'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { Navigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { useLeaderBoardQuery } from '../../api/hooks/useLeaderBoardQuery'
import { LinkButton } from '../../common-components/LinkButton'
import { LoadingPage } from '../loading/loading.page'

export default function LeaderboardCategoryPage() {
  const { data, isLoading, isError, error } = useLeaderBoardQuery('categorized')
  const component = useConfigContext()?.components.leaderboard

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

  if (!component.leaderboardDetailsByCategoryEnabled) return <Navigate to={AbsolutePaths.LEADER_BOARD} />

  const userBoard = component.showUserBoard && (
    <LeaderBoardTable
      data={data?.userBoard || []}
      showGroup={component.showGroupOfUser}
      categorized
      detailed={component.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )
  const groupBoard = component.showGroupBoard && (
    <LeaderBoardTable data={data?.groupBoard || []} detailed={component.leaderboardDetailsEnabled} suffix="pont" categorized />
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

      {component.showUserBoard && component.showGroupBoard ? (
        <Tabs size={{ base: 'sm', md: 'md' }} isFitted={breakpoint !== 'base'} variant="unstyled">
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
