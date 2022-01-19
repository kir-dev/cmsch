import React, { createContext, useEffect, useState } from 'react'
import Cookies from 'js-cookie'
import axios from 'axios'
import { API_BASE_URL } from './configurations'

const CookieKeys = {
  LOGGED_IN: 'loggedIn'
}

export type AuthContextType = {
  isLoggedIn: boolean
  login: () => void
  logout: () => void
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: Cookies.get(CookieKeys.LOGGED_IN) === 'true',
  login: () => {},
  logout: () => {}
})

export const AuthProvider: React.FC = ({ children }) => {
  const [isLoggedIn] = useState<boolean>(Cookies.get(CookieKeys.LOGGED_IN) === 'true')
  const login = () => {
    location.href = '/control/login'
  }
  const logout = () => {
    location.href = '/control/logout'
  }
  useEffect(() => {
    if (isLoggedIn)
      axios.get(`${API_BASE_URL}/api/profile`).then((res) => {
        if (typeof res.data !== 'object') logout()
      })
  }, [isLoggedIn])
  return <AuthContext.Provider value={{ isLoggedIn: isLoggedIn, login: login, logout: logout }}>{children}</AuthContext.Provider>
}
