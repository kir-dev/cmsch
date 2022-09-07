import { Divider, Heading, HStack, useToast } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate } from 'react-router-dom'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useRaceQuery } from '../../api/hooks/useRaceQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { Loading } from '../../common-components/Loading'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { BoardStat } from '../../common-components/BoardStat'

const RacePage = () => {
  const toast = useToast()
  const queryResult = useRaceQuery(() => toast({ title: l('result-query-failed'), status: 'error' }))
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
    return <Loading />
  }

  return (
    <CmschPage>
      <Helmet title={raceComponent.title} />
      <Heading>{raceComponent.title}</Heading>
      <HStack my={5}>
        <BoardStat label="Helyezésed" value={queryResult.data?.place + '.' || '-'} />
        <BoardStat label="Legjobb időd" value={queryResult.data?.bestTime + ' mp' || '-'} />
      </HStack>
      <Divider mb={10} />
      <LeaderBoardTable data={queryResult.data?.board || []} showGroup={true} suffix="mp" />
    </CmschPage>
  )
}

export default RacePage
