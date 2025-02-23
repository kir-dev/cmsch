import { useToast } from '@chakra-ui/react'
import { createContext, PropsWithChildren, useContext, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { AbsolutePaths } from '../../../util/paths'
import { l } from '../../../util/language'

export enum MessageTypes {
  GENERAL = 'general',
  AUTHENTICATION = 'authentication'
}

export interface MessageOptions {
  toast?: boolean
  toastStatus?: 'success' | 'error' | 'warning' | 'info' | undefined
  type?: MessageTypes
  toHomePage?: boolean
}

export type ServiceContextType = {
  sendMessage: (message: string, options?: MessageOptions) => void
  clearMessage: () => void
  message?: string
  type: MessageTypes
}

export const ServiceContext = createContext<ServiceContextType>({
  sendMessage: () => {},
  clearMessage: () => {},
  type: MessageTypes.GENERAL
})

export const ServiceProvider = ({ children }: PropsWithChildren) => {
  const [message, setMessage] = useState<string | undefined>(undefined)
  const [type, setType] = useState<MessageTypes>(MessageTypes.GENERAL)
  const toast = useToast()
  const navigate = useNavigate()

  const sendMessage = (message: string, options?: MessageOptions) => {
    if (options?.toast) {
      toast({ status: options.toastStatus || 'error', title: getToastTitle(options.toastStatus), description: message })
      if (options.toHomePage) navigate('/')
    } else {
      setMessage(message)
      setType(options?.type || MessageTypes.GENERAL)
    }
  }

  useEffect(() => {
    if (message !== undefined) navigate(AbsolutePaths.ERROR)
  }, [message])

  const clearMessage = () => {
    setMessage(undefined)
    setType(MessageTypes.GENERAL)
  }
  return <ServiceContext.Provider value={{ sendMessage, clearMessage, type, message }}>{children}</ServiceContext.Provider>
}

export function getToastTitle(type?: string) {
  switch (type) {
    case 'success':
      return l('toast-title-success')
    case 'error':
      return l('toast-title-error')
    case 'warning':
      return l('toast-title-warning')
    default:
      return l('toast-title-info')
  }
}

export const useServiceContext = () => {
  return useContext<ServiceContextType>(ServiceContext)
}
