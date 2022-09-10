import Cookies from 'js-cookie'
import { createContext, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { queryClient } from '../../../util/configs/api.config'
import { CookieKeys } from '../../../util/configs/cookies.config'
import { API_BASE_URL } from '../../../util/configs/environment.config'
import { AbsolutePaths } from '../../../util/paths'
import { HasChildren } from '../../../util/react-types.util'
import { ProfileView } from '../../../util/views/profile.view'
import { useProfileQuery } from '../../hooks/useProfileQuery'
import { useTokenRefresh } from '../../hooks/useTokenRefresh'

export type AuthContextType = {
  isLoggedIn: boolean
  profile: ProfileView | undefined
  profileLoading: boolean
  profileError: Error | null
  onLoginSuccess: (response: { jwt: string }) => void
  onLoginFailure: (response: any) => void
  onLogout: () => void
  refetch: () => void
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: false,
  profile: undefined,
  profileLoading: false,
  profileError: null,
  onLoginSuccess: () => {},
  onLoginFailure: () => {},
  onLogout: () => {},
  refetch: () => {}
})

export const AuthProvider = ({ children }: HasChildren) => {
  const navigate = useNavigate()

  const onLoginFailure = (err: any) => {
    Cookies.remove(CookieKeys.JWT_TOKEN)
    Cookies.remove(CookieKeys.SESSION_ID)
    queryClient.invalidateQueries('currentUser', { refetchInactive: false })
    console.log('[ERROR] at onLoginFailure', JSON.stringify(err, null, 2))
    navigate('/')
  }

  const onLoginSuccess = async ({ jwt }: { jwt: string }) => {
    Cookies.set(CookieKeys.JWT_TOKEN, jwt, { expires: 2 })
    try {
      await queryClient.invalidateQueries('currentUser', { refetchInactive: true }, { throwOnError: true })
      await queryClient.invalidateQueries('config', { refetchInactive: true }, { throwOnError: true })
      navigate(AbsolutePaths.PROFILE)
    } catch (err) {
      console.log('[ERROR] at onLoginSuccess', JSON.stringify(err, null, 2))
    }
  }

  const onLogout = () => {
    Cookies.remove(CookieKeys.JWT_TOKEN)
    Cookies.remove(CookieKeys.SESSION_ID)
    queryClient.invalidateQueries('currentUser', { refetchInactive: false })
    window.location.href = `${API_BASE_URL}/control/logout`
  }

  const { isLoading: profileLoading, data: profile, error: profileError, refetch } = useProfileQuery()
  const { refresh } = useTokenRefresh(onLoginFailure)

  useEffect(() => {
    if (Cookies.get(CookieKeys.JWT_TOKEN)) {
      refresh((token) => Cookies.set(CookieKeys.JWT_TOKEN, token))
    }
  }, [])

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
        refetch
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
