import { API_BASE_URL } from '@/util/configs/environment.config'
import { ApiPaths } from '@/util/paths'
import { useMutation } from '@tanstack/react-query'
import axios, { AxiosError } from 'axios'

interface ResetPasswordRequest {
  token: string
  newPassword: string
}

interface ResetPasswordResponse {
  status: string
  message?: string
}

export const useResetPasswordMutation = () => {
  return useMutation<ResetPasswordResponse, AxiosError, ResetPasswordRequest>({
    mutationFn: async (data) => {
      const response = await axios.post<ResetPasswordResponse>(`${API_BASE_URL}${ApiPaths.RESET_PASSWORD}`, data)
      return response.data
    }
  })
}
