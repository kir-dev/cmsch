import React from 'react'
import { QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
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
import { Provider } from './components/ui/provider.tsx'

initAxios()

const root = createRoot(document.getElementById('root')!)

root.render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <HelmetProvider>
        <BrowserRouter>
          <ServiceProvider>
            <Provider value={customTheme}>
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
            </Provider>
          </ServiceProvider>
        </BrowserRouter>
      </HelmetProvider>
    </QueryClientProvider>
  </React.StrictMode>
)
