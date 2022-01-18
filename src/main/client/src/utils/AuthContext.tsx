import React, { createContext, useState } from 'react'
import Cookies from 'js-cookie'

const CookieKeys = {
  IS_LOGGED_IN: 'isLoggedIn'
}

export type AuthContextType = {
  isLoggedIn: boolean
  login: () => void
  logout: () => void
}

export const AuthContext = createContext<AuthContextType>({
  isLoggedIn: Cookies.get(CookieKeys.IS_LOGGED_IN) === 'true',
  login: () => {},
  logout: () => {}
})

export const AuthProvider: React.FC = ({ children }) => {
  const [isLoggedIn] = useState<boolean>(localStorage.getItem(CookieKeys.IS_LOGGED_IN) === 'true')
  const login = () => {
    localStorage.setItem(CookieKeys.IS_LOGGED_IN, 'true')
    location.href = '/'
  }
  const logout = () => {
    localStorage.removeItem(CookieKeys.IS_LOGGED_IN)
    location.href = '/'
  }
  return <AuthContext.Provider value={{ isLoggedIn: isLoggedIn, login: login, logout: logout }}>{children}</AuthContext.Provider>
}
