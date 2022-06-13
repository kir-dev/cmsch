import { useToast } from '@chakra-ui/react'
import Cookies from 'js-cookie'
import { createContext, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { queryClient } from '../../../util/configs/api.config'
import { CookieKeys } from '../../../util/configs/cookies.config'
import { HasChildren } from '../../../util/react-types.util'
import { ProfileView } from '../../../util/views/profile.view'
import { useProfileQuery } from '../../hooks/commons/useProfileQuery'

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
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(typeof Cookies.get(CookieKeys.JWT_TOKEN) !== 'undefined')

  const onLoginFailure = (err: any) => {
    console.log('[ERROR] at onLoginFailure', JSON.stringify(err, null, 2))
    toast({
      title: 'Authentikációs hiba',
      description: 'Hiba esett a bejelentkezési folyamatba!',
      status: 'error',
      duration: 5000,
      isClosable: true
    })
  }

  const onLoginSuccess = async ({ jwt }: { jwt: string }) => {
    Cookies.set(CookieKeys.JWT_TOKEN, jwt, { expires: 2 })
    try {
      await queryClient.invalidateQueries('currentUser', { refetchInactive: true }, { throwOnError: true })
      setIsLoggedIn(true)
      navigate('/profil')
    } catch (err) {
      console.log('[ERROR] at onLoginSuccess', JSON.stringify(err, null, 2))
    }
  }

  const onLogout = () => {
    Cookies.remove(CookieKeys.JWT_TOKEN)
    setIsLoggedIn(false)
    queryClient.invalidateQueries('currentUser', { refetchInactive: true })
    toast({
      title: 'Kijelentkezés',
      description: 'Sikeres kijelentkeztetés!',
      status: 'success',
      duration: 5000,
      isClosable: true
    })
  }

  const { isLoading: profileLoading, data: profile, error: profileError } = useProfileQuery(isLoggedIn, onLoginFailure)

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
