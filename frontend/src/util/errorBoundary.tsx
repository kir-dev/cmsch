import React, { ErrorInfo, PropsWithChildren } from 'react'
import { Helmet } from 'react-helmet-async'
import { ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { LinkButton } from '../common-components/LinkButton'
import { CmschPage } from '../common-components/layout/CmschPage'
import { l } from './language'

interface State {
  hasError: boolean
  error?: any
}

export class ErrorBoundary extends React.Component<PropsWithChildren, State> {
  public state: State = {
    hasError: false,
    error: undefined
  }

  static getDerivedStateFromError(err: Error) {
    return {
      hasError: true,
      error: { error: err, cause: err.cause, message: err.message, name: err.name, stack: err.stack }
    }
  }

  componentDidCatch(_: Error, errorInfo: ErrorInfo) {
    console.error(errorInfo)
  }

  render() {
    console.log({ prod: import.meta.env.PROD })
    if (this.state.hasError) {
      return (
        <CmschPage>
          <Helmet title="Hiba" />
          <Heading textAlign="center">{l('error-boundary-title')}</Heading>
          <Text textAlign="center" color="gray.500" marginTop={10}>
            {l('error-boundary-message')}
          </Text>
          {!import.meta.env.PROD &&
            Object.entries(this.state.error).map((err) => (
              <pre style={{ whiteSpace: 'pre-wrap' }}>
                {err[0]}: {String(err[1])}
              </pre>
            ))}
          <ButtonGroup justifyContent="center" marginTop={10}>
            <LinkButton
              onClick={() => {
                this.setState({ hasError: false })
              }}
              href="/"
              colorScheme="brand"
            >
              Főoldal
            </LinkButton>
          </ButtonGroup>
        </CmschPage>
      )
    }

    return this.props.children
  }
}
