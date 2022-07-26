import { ToastId, useToast, UseToastOptions } from '@chakra-ui/react'
import Cookies from 'js-cookie'
import { createContext, useEffect, useRef, useState } from 'react'
import { CookieConsentPopup } from '../../../common-components/cookies/CookieConsentPopup'
import { CookieKeys } from '../../../util/configs/cookies.config'
import { SHOW_COOKIE_CONSENT_POPUP } from '../../../util/configs/environment.config'
import { HasChildren } from '../../../util/react-types.util'

export type CookieConsentContextType = {
  isAccepted: boolean
}

export const CookieConsentContext = createContext<CookieConsentContextType>({
  isAccepted: false
})

export const CookieConsentProvider = ({ children }: HasChildren) => {
  const toast = useToast()
  const toastIdRef = useRef<ToastId>()
  const [isAccepted, setIsAccepted] = useState<boolean>(Cookies.get(CookieKeys.COOKIE_CONSENTED) === 'true')

  const toastOptions: UseToastOptions = {
    containerStyle: {
      width: '100%',
      maxWidth: '100%'
    },
    render: () => <CookieConsentPopup onClick={() => setIsAccepted(true)} />,
    duration: null,
    isClosable: true
  }

  useEffect(() => {
    Cookies.set(CookieKeys.COOKIE_CONSENTED, isAccepted ? 'true' : 'false')
    if (isAccepted && toastIdRef.current) {
      toast.close(toastIdRef.current)
    }
  }, [isAccepted])

  useEffect(() => {
    if (SHOW_COOKIE_CONSENT_POPUP === 'true' && !isAccepted) {
      toastIdRef.current = toast(toastOptions)
    }
  }, [])

  return <CookieConsentContext.Provider value={{ isAccepted }}>{children}</CookieConsentContext.Provider>
}
