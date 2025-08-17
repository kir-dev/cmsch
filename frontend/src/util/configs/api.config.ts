import { QueryClient } from '@tanstack/react-query'
import axios from 'axios'
import { API_BASE_URL } from './environment.config'

export const initAxios = () => {
  axios.defaults.baseURL = API_BASE_URL
  axios.defaults.withCredentials = true
}

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false
    }
  }
})
