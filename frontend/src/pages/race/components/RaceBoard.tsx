import { Divider, Flex, Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Race } from '../../../api/contexts/config/types'
import { BoardStat } from '../../../common-components/BoardStat'
import { ComponentUnavailable } from '../../../common-components/ComponentUnavailable'
import { CmschPage } from '../../../common-components/layout/CmschPage'
import { LeaderBoardTable } from '../../../common-components/LeaderboardTable'
import Markdown from '../../../common-components/Markdown'
import { PageStatus } from '../../../common-components/PageStatus'
import { LeaderBoardItemView } from '../../../util/views/leaderBoardView'
import { RaceView } from '../../../util/views/race.view'

type Props = {
  data: RaceView | undefined
  component: Race
  isError: boolean
  isLoading: boolean
}

const RaceBoard = ({ data, component, isError, isLoading }: Props) => {
  if (!component || !component.visible) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />
  let dataMock: LeaderBoardItemView[] = [
    {
      name: 'Kiss Béla',
      groupName: 'Kisokos'
    },
    {
      name: 'Kiss Béla',
      groupName: 'Kisokos'
    },
    {
      name: 'Kiss Béla',
      groupName: 'Kisokos'
    }
  ]
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
      <LeaderBoardTable data={dataMock} showGroup={true} suffix="mp" />
    </CmschPage>
  )
}

export default RaceBoard