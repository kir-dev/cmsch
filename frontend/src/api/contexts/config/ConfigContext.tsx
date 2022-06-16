import { createContext, useContext, useEffect, useState } from 'react'
import { HasChildren } from '../../../util/react-types.util'
import axios from 'axios'
import { API_BASE_URL } from '../../../util/configs/environment.config'
import { ConfigDto } from './types'

export const ConfigContext = createContext<ConfigDto | undefined>(undefined)

export const ConfigProvider = ({ children }: HasChildren) => {
  const [config, setConfig] = useState<ConfigDto>()
  const [loading, setLoading] = useState<boolean>(true)
  useEffect(() => {
    axios
      .get<ConfigDto>(API_BASE_URL + '/app')
      .then((res) => {
        setConfig({ ...res.data, theme: { brandColor: '#ff7800' } })
      })
      .catch(() => console.error('Theme fetch failed!'))
      .finally(() => setLoading(false))
  }, [])
  // TODO: add loading screen
  if (loading) return null
  return <ConfigContext.Provider value={config}>{children}</ConfigContext.Provider>
}

export const useConfigContext = () => useContext<ConfigDto | undefined>(ConfigContext)
