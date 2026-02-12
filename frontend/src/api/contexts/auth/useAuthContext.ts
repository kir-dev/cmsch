import { useContext } from 'react'
import { AuthContext, type AuthContextType } from './AuthContext'

export const useAuthContext = () => {
  return useContext<AuthContextType>(AuthContext)
}
