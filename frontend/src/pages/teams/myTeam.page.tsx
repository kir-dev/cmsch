import { TeamDetailsCore } from './components/TeamDetailsCore'
import { useMyTeam } from '../../api/hooks/team/queries/useMyTeam'

export default function MyTeamPage() {
  const { data: team, isLoading, error, refetch } = useMyTeam()
  return <TeamDetailsCore team={team} isLoading={isLoading} error={error?.message} myTeam refetch={refetch} />
}
