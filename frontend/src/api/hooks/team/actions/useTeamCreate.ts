import { type CreateTeamDto, TeamResponses } from '@/util/views/team.view'
import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

export const useTeamCreate = (onResponse: (response: TeamResponses) => void) => {
  return useMutation<TeamResponses, Error, CreateTeamDto>({
    mutationFn: async (body) => {
      const res = await axios.post<TeamResponses>(`/api/team/create`, body)
      return res.data
    },
    onSuccess: (data) => {
      onResponse(data)
    },
    onError: (err) => {
      onResponse(TeamResponses.ERROR)
      console.error(err)
    }
  })
}
