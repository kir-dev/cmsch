import { Navigate, useParams } from 'react-router-dom'

import { useTeamDetails } from '../../api/hooks/team/queries/useTeamDetails'
import { TeamDetailsCore } from './components/TeamDetailsCore'
import { AbsolutePaths } from '../../util/paths'
import { TeamStatus } from '../../util/views/team.view'
import { TeamIsNotPlaying } from './components/TeamIsNotPlaying'

export default function TeamDetailsPage() {
  const { id } = useParams()
  const { data: optionalTeam, isLoading, error, refetch } = useTeamDetails(id)
  if (optionalTeam?.status === TeamStatus.NOT_VISIBLE) {
    return <TeamIsNotPlaying />
  }

  const team = optionalTeam?.team
  if (team?.ownTeam) return <Navigate to={AbsolutePaths.MY_TEAM} replace={true} />
  return <TeamDetailsCore team={team} isLoading={isLoading} error={error?.message} refetch={refetch} />
}
