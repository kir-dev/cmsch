import { createContext, type PropsWithChildren, useContext, useEffect } from 'react'
import { LoadingView } from '../../../util/LoadingView.tsx'
import { usePersistentStyleSetting } from '../../../util/configs/themeStyle.config.ts'
import { l } from '../../../util/language.ts'
import { useConfigQuery } from '../../hooks/config/useConfigQuery'
import type { ConfigDto } from './types'

// eslint-disable-next-line react-refresh/only-export-components
export const ConfigContext = createContext<ConfigDto | undefined>(undefined)

export const ConfigProvider = ({ children }: PropsWithChildren) => {
  const { data, isLoading, error, isError, refetch } = useConfigQuery()

  useEffect(() => {
    if (isError) console.error('[ERROR] at ConfigProvider', JSON.stringify(error, null, 2))
  }, [isError, error])

  const style = data?.components?.style
  const { persistentStyle, setPersistentStyle } = usePersistentStyleSetting()
  useEffect(() => {
    // style should always be truthy if there is value
    if (style && style != persistentStyle) {
      setPersistentStyle(style)
    }
  }, [style, persistentStyle, setPersistentStyle])

  const is500Status = Math.floor(Number(error?.response?.status) / 100) === 5
  return (
    <LoadingView
      isLoading={isLoading || !data}
      hasError={isError}
      errorAction={refetch}
      errorMessage={is500Status ? l('error-service-unavailable') : l('error-connection-unsuccessful')}
      errorTitle={is500Status ? l('error-service-unavailable-title') : l('error-page-title')}
    >
      <ConfigContext.Provider value={data}>{children}</ConfigContext.Provider>
    </LoadingView>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export const useConfigContext = (): ConfigDto | undefined => {
  const ctx = useContext(ConfigContext)

  const isUndefined = typeof ctx === 'undefined'
  useEffect(() => {
    if (isUndefined) {
      const error = new Error('useConfigContext must be used within a ConfigProvider')
      console.trace(error)
      window.processAndReportError?.(error)
    }
  }, [isUndefined])

  return ctx
}

// eslint-disable-next-line react-refresh/only-export-components
export const useStyle = () => {
  const context = useContext(ConfigContext)?.components?.style
  const persistent = usePersistentStyleSetting().persistentStyle
  return context || persistent
}
