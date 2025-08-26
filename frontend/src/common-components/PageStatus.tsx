import { useEffect } from 'react'
import { Navigate } from 'react-router'
import { useServiceContext } from '../api/contexts/service/ServiceContext'
import { LoadingPage } from '../pages/loading/loading.page'
import { l } from '../util/language'
import { AbsolutePaths } from '../util/paths'

interface PageStatusProps {
  isLoading: boolean
  isError: boolean
  title?: string
}

export function PageStatus({ isLoading, isError, title }: PageStatusProps) {
  const { sendMessage } = useServiceContext()

  useEffect(() => {
    if (isLoading) return
    if (isError) {
      sendMessage(l('page-load-failed', { title }))
      return
    }

    sendMessage(l('page-load-failed-contact-developers', { title }))
  }, [isLoading, isError, sendMessage, title])

  if (isLoading) {
    return <LoadingPage />
  }

  if (isError) {
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  return <Navigate replace to={AbsolutePaths.ERROR} />
}
