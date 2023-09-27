import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useFreestyleRaceQuery } from '../../api/hooks/race/useFreestyleRaceQuery'
import RaceBoard from './components/RaceBoard'

const FreestyleRace = () => {
  const { isLoading, isError, data } = useFreestyleRaceQuery()
  const component = useConfigContext().components.race
  return <RaceBoard data={data} component={component} isLoading={isLoading} isError={isError} />
}

export default FreestyleRace
