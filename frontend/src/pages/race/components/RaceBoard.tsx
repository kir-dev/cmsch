import { Divider, Flex, Heading } from '@chakra-ui/react'
import { useMemo } from 'react'
import { Helmet } from 'react-helmet-async'
import { Race } from '../../../api/contexts/config/types'
import { BoardStat } from '../../../common-components/BoardStat'
import { ComponentUnavailable } from '../../../common-components/ComponentUnavailable'
import { CmschPage } from '../../../common-components/layout/CmschPage'
import { LeaderBoardTable } from '../../../common-components/LeaderboardTable'
import Markdown from '../../../common-components/Markdown'
import { PageStatus } from '../../../common-components/PageStatus'
import { RaceView } from '../../../util/views/race.view'

type Props = {
  data: RaceView | undefined
  component: Race
  isError: boolean
  isLoading: boolean
}

const RaceBoard = ({ data, component, isError, isLoading }: Props) => {
  const showDescription = useMemo(() => data?.board.some((i) => !!i.description), [data?.board])

  if (!component || !component.visible) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage>
      <Helmet title={data.categoryName} />
      <Heading mb={3}>{data.categoryName}</Heading>
      <Markdown text={data.description || component.defaultCategoryDescription} />
      <Flex my={5} gap={5} flexWrap="wrap">
        <BoardStat label="Helyezésed" value={data.place || '-'} />
        <BoardStat label="Legjobb időd" value={(data.bestTime || '-') + ' mp'} />
      </Flex>
      <Divider mb={10} />
      <LeaderBoardTable
        searchEnabled={component.searchEnabled}
        data={data.board || []}
        showGroup={true}
        suffix="mp"
        showDescription={showDescription}
      />
    </CmschPage>
  )
}
export default RaceBoard
