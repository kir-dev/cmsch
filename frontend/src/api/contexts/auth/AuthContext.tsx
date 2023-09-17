import Cookies from 'js-cookie'
import { createContext, PropsWithChildren, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { initAxios, queryClient } from '../../../util/configs/api.config'
import { CookieKeys } from '../../../util/configs/cookies.config'
import { API_BASE_URL } from '../../../util/configs/environment.config'
import { AbsolutePaths } from '../../../util/paths'
import { ProfileView } from '../../../util/views/profile.view'
import { useProfileQuery } from '../../hooks/profile/useProfileQuery'
import { useTokenRefresh } from '../../hooks/useTokenRefresh'
import { QueryKeys } from '../../hooks/queryKeys'

export type AuthContextType = {
  isLoggedIn: boolean
  profile: ProfileView | undefined
  profileLoading: boolean
  profileError: Error | null
  onLoginSuccess: (response: { jwt: string }) => void
  onLoginFailure: (response: any) => void
  onLogout: () => void
  refetch: () => void
  refreshToken: (onSuccess: (token: string) => void) => void
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: false,
  profile: undefined,
  profileLoading: false,
  profileError: null,
  onLoginSuccess: () => {},
  onLoginFailure: () => {},
  onLogout: () => {},
  refetch: () => {},
  refreshToken: (_) => {}
})

export const AuthProvider = ({ children }: PropsWithChildren) => {
  const navigate = useNavigate()

  const onLoginFailure = (err: any) => {
    Cookies.remove(CookieKeys.JWT_TOKEN)
    Cookies.remove(CookieKeys.SESSION_ID)
    queryClient.invalidateQueries(QueryKeys.USER, { refetchInactive: false })
    console.error('Login failure')
    navigate('/')
  }

  const onLoginSuccess = async ({ jwt }: { jwt: string }) => {
    Cookies.set(CookieKeys.JWT_TOKEN, jwt, { expires: 2 })
    try {
      await queryClient.invalidateQueries(QueryKeys.USER, { refetchInactive: true }, { throwOnError: true })
      await queryClient.invalidateQueries(QueryKeys.CONFIG, { refetchInactive: true }, { throwOnError: true })
      navigate(AbsolutePaths.PROFILE)
    } catch (err) {
      console.error('Login failure')
    }
  }

  const onLogout = () => {
    Cookies.remove(CookieKeys.JWT_TOKEN)
    Cookies.remove(CookieKeys.SESSION_ID)
    window.location.href = `${API_BASE_URL}/control/logout`
  }

  const { isLoading: profileLoading, data: profile, error: profileError, refetch } = useProfileQuery()
  const { refresh } = useTokenRefresh(onLoginFailure)

  useEffect(() => {
    if (Cookies.get(CookieKeys.JWT_TOKEN)) {
      refresh((token) => Cookies.set(CookieKeys.JWT_TOKEN, token))
    }
  }, [])

  const refreshToken = (onSuccess: (token: string) => void) => {
    refresh((token) => {
      Cookies.set(CookieKeys.JWT_TOKEN, token)
      initAxios()
      onSuccess(token)
    })
  }

  return (
    <AuthContext.Provider
      value={{
        isLoggedIn: profile?.loggedIn || false,
        profileLoading,
        profile,
        profileError,
        onLoginSuccess,
        onLoginFailure,
        onLogout,
        refetch,
        refreshToken
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
