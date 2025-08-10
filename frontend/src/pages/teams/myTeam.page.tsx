import { useMyTeam } from '../../api/hooks/team/queries/useMyTeam'
import { TeamStatus } from '../../util/views/team.view'
import { TeamDetailsCore } from './components/TeamDetailsCore'
import { TeamIsNotPlaying } from './components/TeamIsNotPlaying'

export default function MyTeamPage() {
  const { data: optionalTeam, isLoading, error, refetch } = useMyTeam()
  if (optionalTeam?.status === TeamStatus.NOT_VISIBLE) {
    return <TeamIsNotPlaying />
  }

  const team = optionalTeam?.team
  return <TeamDetailsCore team={team} isLoading={isLoading} error={error?.message} myTeam refetch={refetch} />
}
