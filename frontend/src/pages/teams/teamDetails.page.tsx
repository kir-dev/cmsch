import { useParams } from 'react-router'

import { useTeamDetails } from '@/api/hooks/team/queries/useTeamDetails'
import { TeamStatus } from '@/util/views/team.view'
import { TeamDetailsCore } from './components/TeamDetailsCore'
import { TeamIsNotPlaying } from './components/TeamIsNotPlaying'

const DefaultTeam = 'my'

export function TeamDetails({ id }: { id?: string }) {
  const { data: optionalTeam, isLoading, error } = useTeamDetails(id ?? DefaultTeam)
  if (optionalTeam?.status === TeamStatus.NOT_VISIBLE) {
    return <TeamIsNotPlaying />
  }

  const team = optionalTeam?.team
  return <TeamDetailsCore team={team} isLoading={isLoading} error={error?.message} />
}

export default function TeamDetailsPage() {
  const { id } = useParams()
  return <TeamDetails id={id} />
}
