import { useContext } from 'react'
import { CookieConsentContext, CookieConsentContextType } from './CookieConsentContext'

export const useCookieConsentContext = () => {
  return useContext<CookieConsentContextType>(CookieConsentContext)
}
