import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { TeamResponses } from '@/util/views/team.view'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'

export const useTeamJoin = (onResponse: (response: TeamResponses) => void) => {
  const queryClient = useQueryClient()
  return useMutation<TeamResponses, Error, number>({
    mutationFn: async (id) => {
      const res = await axios.post<TeamResponses>(`/api/team/join`, { id })
      return res.data
    },
    onSuccess: async (data) => {
      onResponse(data)
      await queryClient.invalidateQueries({ queryKey: [QueryKeys.TEAM_DETAILS] })
    },
    onError: (err) => {
      onResponse(TeamResponses.ERROR)
      console.error(err)
    }
  })
}
