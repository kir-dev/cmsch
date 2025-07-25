import { l } from '../util/language'
import { Navigate } from 'react-router'
import { AbsolutePaths } from '../util/paths'
import { useServiceContext } from '../api/contexts/service/ServiceContext'
import { useEffect } from 'react'
import { useAuthContext } from '../api/contexts/auth/useAuthContext'
import { LoadingPage } from '../pages/loading/loading.page'
import { LoginRequired } from './LoginRequired'

export function ComponentUnavailable() {
  const { sendMessage } = useServiceContext()
  const { isLoggedIn, authInfoLoading } = useAuthContext()
  useEffect(() => {
    if (isLoggedIn) sendMessage(l('component-unavailable'))
  }, [])
  if (!isLoggedIn) {
    if (authInfoLoading) return <LoadingPage />
    return <LoginRequired />
  }
  return <Navigate to={AbsolutePaths.ERROR} />
}
