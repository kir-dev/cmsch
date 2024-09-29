import { createContext, PropsWithChildren, useEffect } from 'react'
import { API_BASE_URL } from '../../../util/configs/environment.config'
import { useAuthInfo } from '../../hooks/auth/useAuthInfo.ts'
import { AuthState, UserAuthInfoView } from '../../../util/views/authInfo.view.ts'
import { useTokenRefresh } from '../../hooks/useTokenRefresh.ts'

export type AuthContextType = {
  isLoggedIn: boolean
  authInfo: UserAuthInfoView | undefined
  authInfoLoading: boolean
  authInfoError: Error | null
  onLogout: () => void
  refetch: () => void
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: false,
  authInfo: undefined,
  authInfoLoading: false,
  authInfoError: null,
  onLogout: () => {},
  refetch: () => {}
})

export const AuthProvider = ({ children }: PropsWithChildren) => {
  const { isLoading: authInfoLoading, data: authInfo, error: authInfoError, refetch } = useAuthInfo()
  const onLogout = async () => {
    window.location.href = `${API_BASE_URL}/control/logout`
  }
  const tokenRefresh = useTokenRefresh()
  const authState = authInfo?.authState
  useEffect(() => {
    if (authState === AuthState.EXPIRED) {
      tokenRefresh.mutate()
    }
  }, [authState])

  return (
    <AuthContext.Provider
      value={{
        isLoggedIn: authInfo?.authState === AuthState.LOGGED_IN,
        authInfoLoading,
        authInfo,
        authInfoError,
        onLogout,
        refetch
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
