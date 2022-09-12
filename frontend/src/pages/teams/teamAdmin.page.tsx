import { TeamDetailsCore } from './components/teamDetailsCore'
import { useMyTeam } from '../../api/hooks/team/queries/useMyTeam'

export default function TeamAdminPage() {
  const { data: team, isLoading, error, refetch } = useMyTeam()
  return <TeamDetailsCore team={team} isLoading={isLoading} error={error?.message} admin refetch={refetch} />
}
