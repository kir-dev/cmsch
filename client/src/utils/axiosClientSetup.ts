import axios from 'axios'
import Cookies from 'js-cookie'
import { CookieKeys } from './AuthContext'
import { API_BASE_URL } from './configurations'

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
