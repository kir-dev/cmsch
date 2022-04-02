import { useContext } from 'react'
import { ServiceContext, ServiceContextType } from './ServiceContext'

export const useServiceContext = () => {
  return useContext<ServiceContextType>(ServiceContext)
}
