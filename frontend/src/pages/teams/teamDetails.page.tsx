import { useParams } from 'react-router-dom'

import { useTeamDetails } from '../../api/hooks/team/useTeamDetails'
import { TeamDetailsCore } from './components/teamDetailsCore'

export default function TeamDetailsPage() {
  const { id } = useParams()
  const { data: team, isLoading, error } = useTeamDetails(id)
  return <TeamDetailsCore team={team} isLoading={isLoading} error={error?.message} />
}
