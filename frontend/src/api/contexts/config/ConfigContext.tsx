import { createContext, PropsWithChildren, useContext, useEffect } from 'react'
import { useConfigQuery } from '../../hooks/config/useConfigQuery'
import { ConfigDto } from './types'
import { LoadingView } from '../../../util/LoadingView.tsx'
import { l } from '../../../util/language.ts'

export const ConfigContext = createContext<ConfigDto | undefined>(undefined)

export const ConfigProvider = ({ children }: PropsWithChildren) => {
  const { data, isLoading, error, isError, refetch } = useConfigQuery()

  useEffect(() => {
    if (isError) console.error('[ERROR] at ConfigProvider', JSON.stringify(error, null, 2))
  }, [isError, error])

  const is500Status = Math.floor(Number(error?.response?.status) / 100) === 5
  return (
    <LoadingView
      isLoading={isLoading}
      hasError={isError}
      errorAction={refetch}
      errorMessage={is500Status ? l('error-service-unavailable') : l('error-connection-unsuccessful')}
      errorTitle={is500Status ? l('error-service-unavailable-title') : l('error-page-title')}
    >
      <ConfigContext.Provider value={data}>{children}</ConfigContext.Provider>
    </LoadingView>
  )
}

export const useConfigContext = () => {
  const ctx = useContext(ConfigContext)
  if (typeof ctx === 'undefined') {
    throw new Error('useConfigContext must be used within a ConfigProvider')
  }
  return ctx
}
