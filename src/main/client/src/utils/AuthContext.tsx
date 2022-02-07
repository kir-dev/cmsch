import React, { createContext, useEffect, useState } from 'react'
import Cookies from 'js-cookie'
import axios from 'axios'
import { API_BASE_URL } from './configurations'
import { ProfileDTO } from '../types/dto/profile'
import { useServiceContext } from './useServiceContext'

const CookieKeys = {
  LOGGED_IN: 'loggedIn'
}

export type AuthContextType = {
  isLoggedIn: boolean
  profile: ProfileDTO | undefined
  login: () => void
  logout: () => void
  updateProfile: () => void
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: Cookies.get(CookieKeys.LOGGED_IN) === 'true',
  profile: undefined,
  login: () => {},
  logout: () => {},
  updateProfile: () => {}
})

export const AuthProvider: React.FC = ({ children }) => {
  const { throwError } = useServiceContext()
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(Cookies.get(CookieKeys.LOGGED_IN) === 'true')
  const [profile, setProfile] = useState<ProfileDTO>()

  const login = () => {
    location.href = '/control/login'
  }

  const logout = () => {
    location.href = '/control/logout'
  }

  const updateProfile = () => {
    if (isLoggedIn)
      axios
        .get<ProfileDTO>(`${API_BASE_URL}/api/profile`)
        .then((res) => {
          if (typeof res.data !== 'object') logout()
          setProfile(res.data)
        })
        .catch(() => {
          throwError('Újra be kell jelentkezned!', { toast: true, toastStatus: 'warning', toHomePage: true })
          setIsLoggedIn(false)
          setProfile(undefined)
        })
  }

  useEffect(() => {
    updateProfile()
  }, [isLoggedIn])

  return <AuthContext.Provider value={{ isLoggedIn, profile, login, logout, updateProfile }}>{children}</AuthContext.Provider>
}
