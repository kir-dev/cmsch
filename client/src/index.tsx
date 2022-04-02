import { ChakraProvider, ColorModeScript } from '@chakra-ui/react'
import { IndexLayout } from 'components/@layout/IndexLayout'
import React from 'react'
import ReactDOM from 'react-dom'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from 'utils/AuthContext'
import { initAxios } from 'utils/axiosClientSetup'
import customTheme from 'utils/customTheme'
import { ServiceProvider } from 'utils/ServiceContext'
import { App } from './App'
import reportWebVitals from './reportWebVitals'
import * as serviceWorker from './serviceWorker'

initAxios()
ReactDOM.render(
  <React.StrictMode>
    <ColorModeScript />
    <ChakraProvider theme={customTheme}>
      <BrowserRouter>
        <ServiceProvider>
          <AuthProvider>
            <IndexLayout>
              <App />
            </IndexLayout>
          </AuthProvider>
        </ServiceProvider>
      </BrowserRouter>
    </ChakraProvider>
  </React.StrictMode>,
  document.getElementById('root')
)

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://cra.link/PWA
serviceWorker.unregister()

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()
