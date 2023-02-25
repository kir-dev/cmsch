import { Helmet } from 'react-helmet-async'
import { Divider, Flex, Heading, TabList, TabPanel, TabPanels, Tabs, Text, useBreakpoint, VStack } from '@chakra-ui/react'
import { Navigate } from 'react-router-dom'

import { CustomTab } from '../events/components/CustomTab'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { AbsolutePaths } from '../../util/paths'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { useLeaderBoardQuery } from '../../api/hooks/leaderboard/useLeaderBoardQuery'
import { LinkButton } from '../../common-components/LinkButton'
import { PageStatus } from '../../common-components/PageStatus'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'

export default function LeaderboardCategoryPage() {
  const { data, isLoading, isError } = useLeaderBoardQuery('categorized')
  const component = useConfigContext()?.components.leaderboard
  const breakpoint = useBreakpoint()

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

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
