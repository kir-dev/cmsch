import { Heading, Text } from '@chakra-ui/react'
import React, { ErrorInfo, PropsWithChildren } from 'react'
import { Helmet } from 'react-helmet-async'
import { CmschPage } from '../common-components/layout/CmschPage'
import { l } from './language'

interface State {
  hasError: boolean
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
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

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error(errorInfo)

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const win = window as any
    win.processAndReportError?.call(error.message, error.stack)
  }

  render() {
    if (this.state.hasError) {
      return (
        <CmschPage>
          <Helmet title="Hiba" />
          <Heading textAlign="center">{l('error-boundary-title')}</Heading>
          <Text textAlign="center" marginTop={10}>
            {l('error-boundary-message')}
          </Text>
          {!import.meta.env.PROD &&
            Object.entries(this.state.error).map((err) => (
              <pre style={{ whiteSpace: 'pre-wrap' }}>
                {err[0]}: {String(err[1])}
              </pre>
            ))}
        </CmschPage>
      )
    }

    return this.props.children
  }
}
