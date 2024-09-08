import { createContext, PropsWithChildren } from 'react'
import { API_BASE_URL } from '../../../util/configs/environment.config'
import { ProfileView } from '../../../util/views/profile.view'
import { useProfileQuery } from '../../hooks/profile/useProfileQuery'

export type AuthContextType = {
  isLoggedIn: boolean
  profile: ProfileView | undefined
  profileLoading: boolean
  profileError: Error | null
  onLogout: () => void
  refetch: () => void
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: false,
  profile: undefined,
  profileLoading: false,
  profileError: null,
  onLogout: () => {},
  refetch: () => {}
})

export const AuthProvider = ({ children }: PropsWithChildren) => {
  const onLogout = async () => {
    window.location.href = `${API_BASE_URL}/control/logout`
  }

  const { isLoading: profileLoading, data: profile, error: profileError, refetch } = useProfileQuery()

  return (
    <AuthContext.Provider
      value={{
        isLoggedIn: profile?.loggedIn || false,
        profileLoading,
        profile,
        profileError,
        onLogout,
        refetch
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
