export const BACKEND_BASE_URL = (() => {
  if (process.env.NODE_ENV === 'development' || !process.env.REACT_APP_BACKEND_BASE_URL) return 'http://localhost:8080'
  else return process.env.REACT_APP_BACKEND_BASE_URL
})()
export const API_BASE_URL = process.env.NODE_ENV === 'development' ? 'localhost:8080' : ''
export const KIRDEV_URL = process.env.REACT_APP_KIRDEV_URL || 'https://kir-dev.sch.bme.hu'
export const BUGREPORT_URL = process.env.REACT_APP_BUGREPORT_URL || 'https://kir-dev.sch.bme.hu/about#contact'
export const APP_NAME = process.env.REACT_APP_NAME || 'CMSch'
export const GITHUB_ORG_URL = process.env.REACT_APP_GITHUB_ORG_URL || 'https://github.com/kir-dev/cmsch'
