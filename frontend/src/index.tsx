import { ChakraProvider, ColorModeScript } from '@chakra-ui/react'
import React from 'react'
import { QueryClientProvider } from 'react-query'
import { ReactQueryDevtools } from 'react-query/devtools'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './api/contexts/auth/AuthContext'

import { App } from './App'
import reportWebVitals from './reportWebVitals'
import * as serviceWorker from './serviceWorker'
import { initAxios, queryClient } from './util/configs/api.config'
import { createRoot } from 'react-dom/client'
import { ThemeConfig } from './api/contexts/themeConfig/ThemeConfig'
import { ConfigProvider } from './api/contexts/config/ConfigContext'
import { ServiceProvider } from './api/contexts/service/ServiceContext'
import { customTheme } from './util/configs/theme.config'
import { HelmetProvider } from 'react-helmet-async'
import { ErrorBoundary } from './util/errorBoundary'

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
                      <App />
                      <ReactQueryDevtools />
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

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://cra.link/PWA
serviceWorker.unregister()

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()
