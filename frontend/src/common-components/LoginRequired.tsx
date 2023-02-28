import { Navigate } from 'react-router-dom'

export function LoginRequired() {
  localStorage.setItem('path', location.pathname + location.search)
  return <Navigate to="/login" />
}
