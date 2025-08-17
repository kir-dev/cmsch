import { useEffect } from 'react'
import { Navigate } from 'react-router'
import { useAuthContext } from '../api/contexts/auth/useAuthContext'
import { useServiceContext } from '../api/contexts/service/ServiceContext'
import { LoadingPage } from '../pages/loading/loading.page'
import { l } from '../util/language'
import { AbsolutePaths } from '../util/paths'
import { LoginRequired } from './LoginRequired'

export function ComponentUnavailable() {
  const { sendMessage } = useServiceContext()
  const { isLoggedIn, authInfoLoading } = useAuthContext()
  useEffect(() => {
    if (isLoggedIn) sendMessage(l('component-unavailable'))
  }, [isLoggedIn, sendMessage])
  if (!isLoggedIn) {
    if (authInfoLoading) return <LoadingPage />
    return <LoginRequired />
  }
  return <Navigate to={AbsolutePaths.ERROR} />
}
