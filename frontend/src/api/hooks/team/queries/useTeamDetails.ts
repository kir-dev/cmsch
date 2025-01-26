import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { OptionalTeamView } from '../../../../util/views/team.view'
import { QueryKeys } from '../../queryKeys.ts'

export const useTeamDetails = (id: string) => {
  return useQuery<OptionalTeamView, Error>({
    queryKey: [QueryKeys.TEAM_DETAILS, id],
    queryFn: async () => {
      if (!id) throw new Error('Nincs ID!')
      const response = await axios.get<OptionalTeamView>(`/api/team/${id}`)
      return response.data
    }
  })
}
