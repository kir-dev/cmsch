import { ColorModeScript } from '@chakra-ui/react'
import React from 'react'
import { QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { BrowserRouter } from 'react-router'
import { AuthProvider } from './api/contexts/auth/AuthContext'

import { App } from './App'
import { initAxios, queryClient } from './util/configs/api.config'
import { createRoot } from 'react-dom/client'
import { ThemeConfig } from './api/contexts/themeConfig/ThemeConfig'
import { ConfigProvider } from './api/contexts/config/ConfigContext'
import { ServiceProvider } from './api/contexts/service/ServiceContext'
import { HelmetProvider } from 'react-helmet-async'
import { ErrorBoundary } from './util/errorBoundary'
import { PushNotificationHandler } from './common-components/PushNotificationHandler.tsx'
import { AppBackground } from './common-components/layout/AppBackground.tsx'

initAxios()

const root = createRoot(document.getElementById('root')!)

root.render(
  <React.StrictMode>
    <ColorModeScript />
    <ThemeConfig>
      <AppBackground>
        <QueryClientProvider client={queryClient}>
          <HelmetProvider>
            <BrowserRouter>
              <ServiceProvider>
                <ErrorBoundary>
                  <ConfigProvider>
                    <AuthProvider>
                      <PushNotificationHandler>
                        <App />
                        <ReactQueryDevtools />
                      </PushNotificationHandler>
                    </AuthProvider>
                  </ConfigProvider>
                </ErrorBoundary>
              </ServiceProvider>
            </BrowserRouter>
          </HelmetProvider>
        </QueryClientProvider>
      </AppBackground>
    </ThemeConfig>
  </React.StrictMode>
)
