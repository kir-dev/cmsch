import { Navigate } from 'react-router'

export function LoginRequired() {
  localStorage.setItem('path', location.pathname + location.search)
  return <Navigate to="/login" />
}
