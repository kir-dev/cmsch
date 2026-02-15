import { ColorModeScript } from '@chakra-ui/react'
import { QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import React from 'react'
import { BrowserRouter } from 'react-router'
import { AuthProvider } from './api/contexts/auth/AuthContext'

import { createRoot } from 'react-dom/client'
import { App } from './App'
import { ConfigProvider } from './api/contexts/config/ConfigContext'
import { ServiceProvider } from './api/contexts/service/ServiceContext'
import { ThemeConfig } from './api/contexts/themeConfig/ThemeConfig'
import { PushNotificationHandler } from './common-components/PushNotificationHandler.tsx'
import { AppBackground } from './common-components/layout/AppBackground.tsx'
import { initAxios, queryClient } from './util/configs/api.config'
import { ErrorBoundary } from './util/errorBoundary'

initAxios()

const root = createRoot(document.getElementById('root')!)

root.render(
  <React.StrictMode>
    <ColorModeScript />
    <ThemeConfig>
      <AppBackground>
        <QueryClientProvider client={queryClient}>
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
        </QueryClientProvider>
      </AppBackground>
    </ThemeConfig>
  </React.StrictMode>
)
