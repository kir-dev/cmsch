import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'

import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { API_BASE_URL } from '@/util/configs/environment.config'
import { ApiPaths } from '@/util/paths'

export type LoginStatus =
  | 'OK'
  | 'ERROR'
  | 'INVALID_CAPTCHA'
  | 'MISSING_FIELDS'
  | 'EMAIL_ALREADY_EXISTS'
  | 'EMAIL_NOT_CONFIRMED'
  | 'INVALID_CREDENTIALS'
  | 'DISABLED'
  | 'RATE_LIMITED'
  | 'INVALID_TOKEN'
  | 'TOKEN_EXPIRED'

export interface LoginResponse {
  status: LoginStatus
  message?: string
  emailConfirmed?: boolean
  token?: string
}

export const useLoginPoll = (email: string, password: string, enabled: boolean, onConfirmed: () => void) => {
  const { refetch } = useAuthContext()
  const queryClient = useQueryClient()

  return useQuery<LoginResponse>({
    queryKey: ['login-poll', email, password],
    queryFn: async () => {
      const response = await axios.post<LoginResponse>(`${API_BASE_URL}${ApiPaths.LOGIN}`, { email, password }, { withCredentials: true })
      if (response.data.status === 'OK') {
        queryClient.invalidateQueries({ queryKey: [QueryKeys.WHO_AM_I] })
        refetch()
        onConfirmed()
      }
      return response.data
    },
    enabled: enabled && !!email && !!password,
    refetchInterval: 5000,
    retry: true
  })
}

export const useLoginMutation = (onSuccess: (data: LoginResponse) => void, onError?: (error: any) => void) => {
  const { refetch } = useAuthContext()
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async ({ email, password }: any) => {
      const response = await axios.post<LoginResponse>(`${API_BASE_URL}${ApiPaths.LOGIN}`, { email, password }, { withCredentials: true })
      return response.data
    },
    onSuccess: (data) => {
      if (data.status === 'OK') {
        queryClient.invalidateQueries({ queryKey: [QueryKeys.WHO_AM_I] })
        refetch()
      }
      onSuccess(data)
    },
    onError: (error) => {
      console.error(error)
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      reportError(error)
      onError?.(error)
    }
  })
}

export const useRegisterMutation = (onSuccess: (data: LoginResponse) => void, onError?: (error: any) => void) => {
  const { refetch } = useAuthContext()
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: async ({ email, password, fullName, captchaToken }: any) => {
      const response = await axios.post<LoginResponse>(
        `${API_BASE_URL}${ApiPaths.REGISTER}`,
        { email, password, fullName, captchaToken },
        { withCredentials: true }
      )
      return response.data
    },
    onSuccess: (data) => {
      if (data.status === 'OK' && data.emailConfirmed) {
        queryClient.invalidateQueries({ queryKey: [QueryKeys.WHO_AM_I] })
        refetch()
      }
      onSuccess(data)
    },
    onError: (error) => {
      console.error(error)
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      reportError(error)
      onError?.(error)
    }
  })
}

export const useForgotPasswordMutation = (onSuccess: (data: LoginResponse) => void, onError?: (error: any) => void) => {
  return useMutation({
    mutationFn: async (email: string) => {
      const response = await axios.post<LoginResponse>(`${API_BASE_URL}${ApiPaths.FORGOT_PASSWORD}`, { email }, { withCredentials: true })
      return response.data
    },
    onSuccess: (data) => {
      onSuccess(data)
    },
    onError: (error) => {
      console.error(error)
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      reportError(error)
      onError?.(error)
    }
  })
}
