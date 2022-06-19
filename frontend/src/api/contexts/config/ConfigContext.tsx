import { createContext, useContext } from 'react'
import { HasChildren } from '../../../util/react-types.util'
import { ConfigDto } from './types'
import { useConfigQuery } from '../../hooks/useConfigQuery'

export const ConfigContext = createContext<ConfigDto | undefined>(undefined)

export const ConfigProvider = ({ children }: HasChildren) => {
  const { data, isLoading } = useConfigQuery((err) => console.error('[ERROR] at ConfigProvider', JSON.stringify(err, null, 2)))
  // TODO: add loading screen
  if (isLoading) return null
  return <ConfigContext.Provider value={data}>{children}</ConfigContext.Provider>
}

export const useConfigContext = () => useContext<ConfigDto | undefined>(ConfigContext)
