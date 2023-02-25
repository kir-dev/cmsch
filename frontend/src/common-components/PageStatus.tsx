import { LoadingPage } from '../pages/loading/loading.page'
import { l } from '../util/language'
import { Navigate } from 'react-router-dom'
import { AbsolutePaths } from '../util/paths'
import { useServiceContext } from '../api/contexts/service/ServiceContext'

interface PageStatusProps {
  isLoading: boolean
  isError: boolean
  title?: string
}

export function PageStatus({ isLoading, isError, title }: PageStatusProps) {
  const { sendMessage } = useServiceContext()

  if (isLoading) {
    return <LoadingPage />
  }

  if (isError) {
    sendMessage(l('page-load-failed', { title }))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  sendMessage(l('page-load-failed-contact-developers', { title }))
  return <Navigate replace to={AbsolutePaths.ERROR} />
}
