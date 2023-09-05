import { useQuery } from 'react-query'
import { MockTeamDashboardView } from '../../../../pages/teams/mockTeamDashboardView'
import { TeamDashboardView } from '../../../../util/views/team.view'

export const useTeamDashboard = (onError?: (err: any) => void) => {
  return useQuery<TeamDashboardView, Error>(
    'team-dashboard',
    async () => {
      return MockTeamDashboardView
    },
    { onError }
  )
}
