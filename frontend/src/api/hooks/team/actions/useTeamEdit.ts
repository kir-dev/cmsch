import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { type TeamEditDto, TeamResponses } from '@/util/views/team.view'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'

export const useTeamEdit = (onResponse: (response: TeamResponses) => void) => {
  const queryClient = useQueryClient()
  return useMutation<TeamResponses, Error, TeamEditDto>({
    mutationFn: async (dto) => {
      const data = new FormData()
      data.append('description', dto.description)
      if (dto.logo) {
        data.append('logo', dto.logo)
      }
      const res = await axios.post<TeamResponses>(`/api/team/edit`, data)
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
