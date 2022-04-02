import { useToast } from '@chakra-ui/react'
import { createContext, FC, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getToastTitle } from './toastTitle'

export enum ErrorTypes {
  GENERAL = 'general',
  AUTHENTICATION = 'authentication'
}

export interface ErrorOptions {
  toast?: boolean
  toastStatus?: 'success' | 'error' | 'warning' | 'info' | undefined
  type?: ErrorTypes
  toHomePage?: boolean
}

export type ServiceContextType = {
  throwError: (message: string, options?: ErrorOptions) => void
  clearError: () => void
  error?: string
  errorType: ErrorTypes
}

export const ServiceContext = createContext<ServiceContextType>({
  throwError: () => {},
  clearError: () => {},
  errorType: ErrorTypes.GENERAL
})

export const ServiceProvider: FC = ({ children }) => {
  const [error, setError] = useState<string | undefined>(undefined)
  const [errorType, setErrorType] = useState<ErrorTypes>(ErrorTypes.GENERAL)
  const toast = useToast()
  const navigate = useNavigate()
  const throwError = (message: string, options?: ErrorOptions) => {
    if (options?.toast) {
      toast({ status: options.toastStatus || 'error', title: getToastTitle(options.toastStatus), description: message })
      if (options.toHomePage) navigate('/')
    } else {
      setError(message)
      setErrorType(options?.type || ErrorTypes.GENERAL)
    }
  }
  useEffect(() => {
    if (error !== undefined) navigate('/error')
  }, [error])
  const clearError = () => {
    setError(undefined)
    setErrorType(ErrorTypes.GENERAL)
  }
  return (
    <ServiceContext.Provider value={{ throwError: throwError, clearError: clearError, error: error, errorType: errorType }}>
      {children}
    </ServiceContext.Provider>
  )
}
