import { useMutation, useQuery } from '@tanstack/react-query'
import axios from 'axios'

import { useTokenRefresh } from '@/api/hooks/useTokenRefresh.ts'
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
  | 'WEAK_PASSWORD'

export interface LoginResponse {
  status: LoginStatus
  emailConfirmed?: boolean
}

export const useLoginPoll = (email: string, password: string, enabled: boolean, onConfirmed: () => void) => {
  const tokenRefresh = useTokenRefresh()
  return useQuery<{ confirmed: boolean }>({
    queryKey: ['login-poll', email],
    queryFn: async () => {
      const response = await axios.get<{ confirmed: boolean }>(`${API_BASE_URL}${ApiPaths.AUTH_STATUS}`, {
        params: { email },
        withCredentials: true
      })
      if (response.data.confirmed) {
        const loginResponse = await axios.post<LoginResponse>(
          `${API_BASE_URL}${ApiPaths.LOGIN}`,
          { email, password },
          { withCredentials: true }
        )
        if (loginResponse.data.status === 'OK') {
          await tokenRefresh.mutateAsync()
          onConfirmed()
        }
      }
      return response.data
    },
    enabled: enabled && !!email,
    refetchInterval: 5000,
    retry: true
  })
}

export const useLoginMutation = (onSuccess: (data: LoginResponse) => void, onError?: (error: Error) => void) => {
  const tokenRefresh = useTokenRefresh()
  return useMutation({
    mutationFn: async ({ email, password }: { email: string; password: string }) => {
      const response = await axios.post<LoginResponse>(`${API_BASE_URL}${ApiPaths.LOGIN}`, { email, password }, { withCredentials: true })
      return response.data
    },
    onSuccess: async (data) => {
      if (data.status === 'OK') {
        await tokenRefresh.mutateAsync()
      }
      onSuccess(data)
    },
    onError: (error) => {
      console.error(error)
      window.processAndReportError(error)
      onError?.(error)
    }
  })
}

export const useRegisterMutation = (onSuccess: (data: LoginResponse) => void, onError?: (error: Error) => void) => {
  const tokenRefresh = useTokenRefresh()
  return useMutation({
    mutationFn: async ({
      email,
      password,
      fullName,
      captchaToken
    }: {
      email: string
      password: string
      captchaToken: string | null
      fullName: string
    }) => {
      const response = await axios.post<LoginResponse>(
        `${API_BASE_URL}${ApiPaths.REGISTER}`,
        { email, password, fullName, captchaToken },
        { withCredentials: true }
      )
      return response.data
    },
    onSuccess: async (data) => {
      if (data.status === 'OK' && data.emailConfirmed) {
        await tokenRefresh.mutateAsync()
      }
      onSuccess(data)
    },
    onError: (error) => {
      console.error(error)
      window.processAndReportError(error)
      onError?.(error)
    }
  })
}

export const useForgotPasswordMutation = (onSuccess: (data: LoginResponse) => void, onError?: (error: Error) => void) => {
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
      window.processAndReportError(error)
      onError?.(error)
    }
  })
}
