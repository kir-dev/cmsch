import { MessageTypes, useServiceContext } from '@/api/contexts/service/ServiceContext'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { useBrandColor } from '@/util/core-functions.util.ts'
import { l } from '@/util/language'
import { useEffect, useState } from 'react'
import { Navigate } from 'react-router'
import { UnauthorizedPage } from './unauthorized.page'

type Props = {
  message?: string
}

export const ErrorPage = ({ message: messageProp }: Props) => {
  const { clearMessage, message, type } = useServiceContext()
  const [clonedMessage, setClonedMessage] = useState<string | undefined>('')
  const [clonedMessageType, setClonedMessageType] = useState<MessageTypes>(MessageTypes.GENERAL)
  const brandColor = useBrandColor()
  useEffect(() => {
    // Cloning the error is needed to clear the error globally
    // The message from prop can override the message
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setClonedMessage(messageProp || message)
    setClonedMessageType(type || MessageTypes.GENERAL)
    // Clear the error from the context since the user has already been notified, and prepare for navigation
    clearMessage()
    // No need for deps because the clearError would clear this error page too
  }, [clearMessage, message, messageProp, type])
  // If there is no error ATM, redirect to home page
  if (clonedMessage === undefined) return <Navigate to="/" />
  // Display authentication page for the corresponding error type
  if (clonedMessageType === MessageTypes.AUTHENTICATION) return <UnauthorizedPage />
  return (
    <CmschPage title={l('error-page-helmet')}>
      <h1 className="text-3xl font-bold font-heading text-center">{l('error-page-title')}</h1>
      <div className="text-center text-gray-500 mt-10">
        <Markdown text={clonedMessage} />
      </div>
      <div className="flex justify-center mt-10">
        <LinkButton href="/" style={{ backgroundColor: brandColor }}>
          Főoldal
        </LinkButton>
      </div>
    </CmschPage>
  )
}
