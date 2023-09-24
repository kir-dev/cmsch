import { Box, ButtonGroup, Heading } from '@chakra-ui/react'
import { useEffect, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Navigate } from 'react-router-dom'
import { MessageTypes, useServiceContext } from '../../api/contexts/service/ServiceContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { l } from '../../util/language'
import { UnauthorizedPage } from './unauthorized.page'

type Props = {
  message?: string
}

export const ErrorPage = ({ message: messageProp }: Props) => {
  const { clearMessage, message, type } = useServiceContext()
  const [clonedMessage, setClonedMessage] = useState<string | undefined>('')
  const [clonedMessageType, setClonedMessageType] = useState<MessageTypes>(MessageTypes.GENERAL)
  useEffect(() => {
    // Cloning the error is needed to clear the error globally
    // The message from prop can override the message
    setClonedMessage(messageProp || message)
    setClonedMessageType(type || MessageTypes.GENERAL)
    // Clear the error from the context since the user has already been notified, and prepare for navigation
    clearMessage()
    // No need for deps because the clearError would clear this error page too
  }, [])
  // If there is no error ATM, redirect to home page
  if (clonedMessage === undefined) return <Navigate to="/" />
  // Display authentication page for the corresponding error type
  if (clonedMessageType === MessageTypes.AUTHENTICATION) return <UnauthorizedPage />
  return (
    <CmschPage>
      <Helmet title={l('error-page-helmet')} />
      <Heading textAlign="center">{l('error-page-title')}</Heading>
      <Box textAlign="center" color="gray.500" marginTop={10}>
        <Markdown text={clonedMessage} />
      </Box>
      <ButtonGroup justifyContent="center" marginTop={10}>
        <LinkButton href="/" colorScheme="brand">
          Főoldal
        </LinkButton>
      </ButtonGroup>
    </CmschPage>
  )
}
