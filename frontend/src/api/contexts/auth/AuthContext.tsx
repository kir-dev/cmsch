import { useToast } from '@chakra-ui/react'
import { UserView } from '@triszt4n/remark-types'
import Cookies from 'js-cookie'
import { createContext, FC, useState } from 'react'
import { GoogleLoginResponse, GoogleLoginResponseOffline } from 'react-google-login'
import { useMutation, useQuery } from 'react-query'
import { useNavigate } from 'react-router-dom'
import { queryClient } from '../../../util/query-client'
import { rconsole } from '../../../util/remark-console'
import { userModule } from '../../modules/user.module'
import { CookieKeys } from '../CookieKeys'
import { useNotifContext } from '../notifications/useNotifContext'

export type AuthContextType = {
  isLoggedIn: boolean
  loggedInUser: UserView | undefined
  loggedInUserLoading: boolean
  loggedInUserError: unknown
  onLoginSuccess: (response: GoogleLoginResponseOffline | GoogleLoginResponse) => void
  onLoginFailure: (response: GoogleLoginResponseOffline | GoogleLoginResponse) => void
  onLogout: () => void
  loginLoading: boolean
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: false,
  loggedInUser: undefined,
  loggedInUserLoading: false,
  loggedInUserError: undefined,
  onLoginSuccess: () => {},
  onLoginFailure: () => {},
  onLogout: () => {},
  loginLoading: false
})

export const AuthProvider: FC = ({ children }) => {
  const toast = useToast()
  const navigate = useNavigate()
  const { startNotificationReception, stopNotificationReception } = useNotifContext()

  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(typeof Cookies.get(CookieKeys.REMARK_JWT_TOKEN) !== 'undefined')
  const {
    isLoading,
    data: user,
    error
  } = useQuery('currentUser', userModule.fetchCurrentUser, {
    enabled: !!isLoggedIn
  })

  const mutation = useMutation(userModule.loginUser, {
    onSuccess: async ({ data }) => {
      const { jwt, user } = data
      Cookies.set(CookieKeys.REMARK_JWT_TOKEN, jwt, { expires: 2 })

      await queryClient.invalidateQueries('currentUser', { refetchInactive: true })
      setIsLoggedIn(true)
      navigate('/profile')
    },
    onError: (error) => {
      const err = error as any
      rconsole.log('Error at loginUser', err.toJSON())
      toast({
        title: 'Error occured when logging in new user',
        description: `${err.response.status} ${err.response.data.message} Try again later.`,
        status: 'error',
        isClosable: true
      })
    }
  })

  const onLoginSuccess = (response: GoogleLoginResponseOffline | GoogleLoginResponse) => {
    const { accessToken } = response as GoogleLoginResponse
    mutation.mutate(accessToken)
  }

  const onLoginFailure = (response: GoogleLoginResponseOffline | GoogleLoginResponse) => {
    rconsole.log('Error at onLoginFailure', JSON.stringify(response, null, 2))
    toast({
      title: 'Authentication error',
      description: 'There was an error while authenticating you at Google!',
      status: 'error',
      duration: 5000,
      isClosable: true
    })
  }

  const onLogout = () => {
    Cookies.remove(CookieKeys.REMARK_JWT_TOKEN)
    setIsLoggedIn(false)
    queryClient.invalidateQueries('currentUser', { refetchInactive: true })
    navigate('/')
  }

  return (
    <AuthContext.Provider
      value={{
        isLoggedIn,
        loggedInUserLoading: isLoading,
        loggedInUser: user,
        loggedInUserError: error,
        onLoginSuccess,
        onLoginFailure,
        onLogout,
        loginLoading: mutation.isLoading
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
