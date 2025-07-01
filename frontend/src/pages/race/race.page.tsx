import { useParams } from 'react-router'

import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useRaceQuery } from '../../api/hooks/race/useRaceQuery'
import RaceBoard from './components/RaceBoard'

const RacePage = () => {
  const params = useParams()
  const { isLoading, isError, data } = useRaceQuery(params.category ?? '')
  const component = useConfigContext()?.components.race

  return <RaceBoard data={data} component={component} isLoading={isLoading} isError={isError} />
}

export default RacePage
