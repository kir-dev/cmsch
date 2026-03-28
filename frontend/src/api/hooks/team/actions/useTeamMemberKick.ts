import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'

export const useTeamMemberKick = (onSuccess: (response: boolean) => void, onError = () => {}) => {
  const queryClient = useQueryClient()
  return useMutation<boolean, Error, number>({
    mutationFn: async (id) => {
      const res = await axios.put<boolean>(`/api/team/admin/kick-user`, { id })
      return res.data
    },
    onSuccess: async (data) => {
      onSuccess(data)
      await queryClient.invalidateQueries({ queryKey: [QueryKeys.TEAM_DETAILS] })
    },
    onError: (err) => {
      console.error(err)
      onError()
    }
  })
}
