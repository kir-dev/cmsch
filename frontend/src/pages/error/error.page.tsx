import { ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { useEffect, useState } from 'react'
import { Helmet } from 'react-helmet'
import { Navigate } from 'react-router-dom'
import { MessageTypes, useServiceContext } from '../../api/contexts/service/ServiceContext'
import { LinkButton } from '../../common-components/LinkButton'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { UnauthorizedPage } from './unauthorized.page'

export const ErrorPage = () => {
  const { clearMessage, message, type } = useServiceContext()
  const [clonedMessage, setClonedMessage] = useState<string | undefined>('')
  const [clonedMessageType, setClonedMessageType] = useState<MessageTypes>(MessageTypes.GENERAL)
  useEffect(() => {
    // Cloning the error is needed to clear the error globally
    setClonedMessage(message)
    setClonedMessageType(type || MessageTypes.GENERAL)
    // Clear the error from the context since the user has already been notified, and prepare for navigation
    clearMessage()
    // No need for deps because the clearError would clear this error page too
  }, [])
  // If there is no error ATM, redirect to home page
  if (clonedMessage === undefined) return <Navigate to="/" />
  // Dispaly authentication page for the corresponding error type
  if (clonedMessageType === MessageTypes.AUTHENTICATION) return <UnauthorizedPage />
  return (
    <CmschPage>
      <Helmet title="Hiba" />
      <Heading textAlign="center">Hiba történt</Heading>
      <Text textAlign="center" color="gray.500" marginTop={10}>
        {clonedMessage}
      </Text>
      <ButtonGroup justifyContent="center" marginTop={10}>
        <LinkButton href="/" colorScheme="brand">
          Főoldal
        </LinkButton>
      </ButtonGroup>
    </CmschPage>
  )
}
