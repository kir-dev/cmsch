import axios from 'axios'
import Cookies from 'js-cookie'
import { QueryClient } from 'react-query'
import { CookieKeys } from './cookies.config'
import { API_BASE_URL } from './environment.config'

export const initAxios = () => {
  axios.defaults.baseURL = API_BASE_URL
  axios.interceptors.request.use((config) => {
    const token = Cookies.get(CookieKeys.JWT_TOKEN)
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  })
}

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false
    }
  }
})
