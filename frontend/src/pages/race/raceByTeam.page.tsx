import { useParams } from 'react-router-dom'

import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useRaceByTeamQuery } from '../../api/hooks/race/useRaceByTeamQuery'
import RaceBoard from './components/RaceBoard'

const RaceByTeamPage = () => {
  const params = useParams()
  const { isLoading, isError, data } = useRaceByTeamQuery(params.id ?? '')
  const component = useConfigContext()?.components.race

  return <RaceBoard data={data} component={component} isLoading={isLoading} isError={isError} />
}

export default RaceByTeamPage
