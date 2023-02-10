import { Divider, Flex, Heading, useToast } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useParams } from 'react-router-dom'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useRaceQuery } from '../../api/hooks/useRaceQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { BoardStat } from '../../common-components/BoardStat'
import Markdown from '../../common-components/Markdown'
import { LoadingPage } from '../loading/loading.page'

const RacePage = () => {
  const toast = useToast()
  const params = useParams()
  const queryResult = useRaceQuery(params.category ?? '', () => toast({ title: l('result-query-failed'), status: 'error' }))
  const raceComponent = useConfigContext()?.components.race
  const { sendMessage } = useServiceContext()

  if (!raceComponent || !raceComponent.visible) {
    toast({ title: l('component-unavailable'), status: 'error' })
    return <Navigate replace to="/" />
  }

  if (queryResult.isError) {
    sendMessage(l('result-query-failed') + queryResult.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (queryResult.isLoading) {
    return <LoadingPage />
  }

  return (
    <CmschPage>
      <Helmet title={queryResult.data?.categoryName} />
      <Heading mb={3}>{queryResult.data?.categoryName}</Heading>
      <Markdown text={queryResult.data?.description || raceComponent.defaultCategoryDescription} />
      <Flex my={5} gap={5} flexWrap="wrap">
        <BoardStat label="Helyezésed" value={queryResult.data?.place || '-'} />
        <BoardStat label="Legjobb időd" value={(queryResult.data?.bestTime || '-') + ' mp'} />
      </Flex>
      <Divider mb={10} />
      <LeaderBoardTable data={queryResult.data?.board || []} showGroup={true} suffix="mp" />
    </CmschPage>
  )
}

export default RacePage
