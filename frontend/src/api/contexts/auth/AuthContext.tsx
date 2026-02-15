import { createContext, type PropsWithChildren, useCallback, useEffect, useState } from 'react'
import { API_BASE_URL } from '../../../util/configs/environment.config'
import { AuthState, type UserAuthInfoView } from '../../../util/views/authInfo.view.ts'
import { useAuthInfo } from '../../hooks/auth/useAuthInfo.ts'
import { useTokenRefresh } from '../../hooks/useTokenRefresh.ts'

export type AuthContextType = {
  isLoggedIn: boolean
  authInfo: UserAuthInfoView | undefined
  authInfoLoading: boolean
  authInfoError: Error | null
  onLogout: () => void
  refetch: () => void
}

// eslint-disable-next-line react-refresh/only-export-components
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
  const [shouldLogout, setShouldLogout] = useState(false)

  useEffect(() => {
    if (shouldLogout) {
      window.location.href = `${API_BASE_URL}/control/logout`
    }
  }, [shouldLogout])

  const onLogout = useCallback(async () => {
    setShouldLogout(true)
  }, [])

  const { mutate } = useTokenRefresh()
  const authState = authInfo?.authState
  useEffect(() => {
    if (authState === AuthState.EXPIRED) {
      mutate()
    }
  }, [authState, mutate])

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
