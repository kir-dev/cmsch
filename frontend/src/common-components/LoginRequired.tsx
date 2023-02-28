import { Navigate } from 'react-router-dom'

export function LoginRequired() {
  localStorage.setItem('path', window.location.pathname)
  return <Navigate to="/login" />
}
