import { ChakraProvider, ColorModeScript } from '@chakra-ui/react'
import React from 'react'
import { QueryClientProvider } from 'react-query'
import { ReactQueryDevtools } from 'react-query/devtools'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './api/contexts/auth/AuthContext'

import { App } from './App'
import { initAxios, queryClient } from './util/configs/api.config'
import { createRoot } from 'react-dom/client'
import { ThemeConfig } from './api/contexts/themeConfig/ThemeConfig'
import { ConfigProvider } from './api/contexts/config/ConfigContext'
import { ServiceProvider } from './api/contexts/service/ServiceContext'
import { customTheme } from './util/configs/theme.config'
import { HelmetProvider } from 'react-helmet-async'
import { ErrorBoundary } from './util/errorBoundary'
import { PushNotificationHandler } from './common-components/PushNotificationHandler.tsx'

initAxios()

const root = createRoot(document.getElementById('root')!)

root.render(
  <React.StrictMode>
    <ColorModeScript />
    <QueryClientProvider client={queryClient}>
      <HelmetProvider>
        <BrowserRouter>
          <ServiceProvider>
            <ChakraProvider theme={customTheme}>
              <ConfigProvider>
                <ThemeConfig>
                  <ErrorBoundary>
                    <AuthProvider>
                      <PushNotificationHandler>
                        <App />
                        <ReactQueryDevtools />
                      </PushNotificationHandler>
                    </AuthProvider>
                  </ErrorBoundary>
                </ThemeConfig>
              </ConfigProvider>
            </ChakraProvider>
          </ServiceProvider>
        </BrowserRouter>
      </HelmetProvider>
    </QueryClientProvider>
  </React.StrictMode>
)
