import { CmschPage } from '@/common-components/layout/CmschPage'
import React, { type ErrorInfo, type PropsWithChildren } from 'react'
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

    window.processAndReportError?.(error)
  }

  render() {
    if (this.state.hasError) {
      return (
        <CmschPage title="Hiba">
          <h2 className="mt-5 text-center text-3xl font-bold">{l('error-boundary-title')}</h2>
          <p className="mt-10 text-center">{l('error-boundary-message')}</p>
          {!import.meta.env.PROD &&
            Object.entries(this.state.error).map((err) => (
              <pre key={err[0]} style={{ whiteSpace: 'pre-wrap' }}>
                {err[0]}: {String(err[1])}
              </pre>
            ))}
        </CmschPage>
      )
    }

    return this.props.children
  }
}
