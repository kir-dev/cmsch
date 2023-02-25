import { l } from '../util/language'
import { Navigate } from 'react-router-dom'
import { AbsolutePaths } from '../util/paths'
import { useServiceContext } from '../api/contexts/service/ServiceContext'
import { useEffect } from 'react'

export function ComponentUnavailable() {
  const { sendMessage } = useServiceContext()

  useEffect(() => sendMessage(l('component-unavailable')), [])
  return <Navigate to={AbsolutePaths.ERROR} />
}
