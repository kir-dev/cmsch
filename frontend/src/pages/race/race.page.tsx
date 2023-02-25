import { Divider, Flex, Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useParams } from 'react-router-dom'

import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useRaceQuery } from '../../api/hooks/race/useRaceQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { BoardStat } from '../../common-components/BoardStat'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'

const RacePage = () => {
  const params = useParams()
  const { isLoading, isError, data } = useRaceQuery(params.category ?? '')
  const component = useConfigContext()?.components.race

  if (!component || !component.visible) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage>
      <Helmet title={data?.categoryName} />
      <Heading mb={3}>{data?.categoryName}</Heading>
      <Markdown text={data?.description || component.defaultCategoryDescription} />
      <Flex my={5} gap={5} flexWrap="wrap">
        <BoardStat label="Helyezésed" value={data?.place || '-'} />
        <BoardStat label="Legjobb időd" value={(data?.bestTime || '-') + ' mp'} />
      </Flex>
      <Divider mb={10} />
      <LeaderBoardTable data={data?.board || []} showGroup={true} suffix="mp" />
    </CmschPage>
  )
}

export default RacePage
