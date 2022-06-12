import { useToast } from '@chakra-ui/react'
import Cookies from 'js-cookie'
import { createContext, useState } from 'react'
import { useQuery } from 'react-query'
import { useNavigate } from 'react-router-dom'
import { queryClient } from '../../../util/configs/api.config'
import { CookieKeys } from '../../../util/configs/cookies.config'
import { HasChildren } from '../../../util/react-types.util'
import { ProfileView } from '../../../util/views/profile.view'

export type AuthContextType = {
  isLoggedIn: boolean
  profile: ProfileView | undefined
  profileLoading: boolean
  profileError: unknown
  onLoginSuccess: (response: { jwt: string }) => void
  onLoginFailure: (response: any) => void
  onLogout: () => void
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: false,
  profile: undefined,
  profileLoading: false,
  profileError: undefined,
  onLoginSuccess: () => {},
  onLoginFailure: () => {},
  onLogout: () => {}
})

export const AuthProvider = ({ children }: HasChildren) => {
  const toast = useToast()
  const navigate = useNavigate()

  // TODO: profile fetch logic
  const fetchProfile = () => {
    return undefined
  }
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(typeof Cookies.get(CookieKeys.JWT_TOKEN) !== 'undefined')
  const { isLoading: profileLoading, data: profile, error: profileError } = useQuery('currentUser', fetchProfile, { enabled: !!isLoggedIn })

  const onLoginSuccess = async ({ jwt }: { jwt: string }) => {
    Cookies.set(CookieKeys.JWT_TOKEN, jwt, { expires: 2 })
    await queryClient.invalidateQueries('currentUser', { refetchInactive: true })
    setIsLoggedIn(true)
    navigate('/profile')
  }

  const onLoginFailure = (response: any) => {
    console.log('[ERROR] at onLoginFailure', JSON.stringify(response, null, 2))
    toast({
      title: 'Authentikációs hiba',
      description: 'Hiba esett a bejelentkezési folyamatba!',
      status: 'error',
      duration: 5000,
      isClosable: true
    })
  }

  const onLogout = () => {
    Cookies.remove(CookieKeys.JWT_TOKEN)
    setIsLoggedIn(false)
    queryClient.invalidateQueries('currentUser', { refetchInactive: true })
    navigate('/')
  }

  return (
    <AuthContext.Provider
      value={{
        isLoggedIn,
        profileLoading,
        profile,
        profileError,
        onLoginSuccess,
        onLoginFailure,
        onLogout
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
