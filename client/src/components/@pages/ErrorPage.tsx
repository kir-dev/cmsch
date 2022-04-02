import React, { useEffect, useState } from 'react'
import { Page } from '../@layout/Page'
import { ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { useServiceContext } from '../../utils/useServiceContext'
import { LinkButton } from '../@commons/LinkButton'
import { ErrorTypes } from '../../utils/ServiceContext'
import { UnauthorizedPage } from './UnauthorizedPage'
import { Navigate } from 'react-router-dom'
import { Helmet } from 'react-helmet'

export const ErrorPage: React.FC = () => {
  const { clearError, error, errorType } = useServiceContext()
  const [clonedError, setClonedError] = useState<string | undefined>('')
  const [clonedErrorType, setClonedErrorType] = useState<ErrorTypes>(ErrorTypes.GENERAL)
  useEffect(() => {
    // Cloning the error is needed to clear the error globally
    setClonedError(error)
    setClonedErrorType(errorType || ErrorTypes.GENERAL)
    // Clear the error from the context since the user has already been notified, and prepare for navigation
    clearError()
    // No need for deps because the clearError would clear this error page too
  }, [])
  // If there is no error ATM, redirect to home page
  if (clonedError === undefined) return <Navigate to="/" />
  // Dispaly authentication page for the corresponding error type
  if (clonedErrorType === ErrorTypes.AUTHENTICATION) return <UnauthorizedPage />
  return (
    <Page>
      <Helmet title="Hiba" />
      <Heading textAlign="center">Hiba történt</Heading>
      <Text textAlign="center" color="gray.500" marginTop={10}>
        {clonedError}
      </Text>
      <ButtonGroup justifyContent="center" marginTop={10}>
        <LinkButton href="/" colorScheme="brand">
          Főoldal
        </LinkButton>
      </ButtonGroup>
    </Page>
  )
}
