import React, { ErrorInfo, PropsWithChildren } from 'react'
import { Helmet } from 'react-helmet-async'
import { ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { LinkButton } from '../common-components/LinkButton'
import { CmschPage } from '../common-components/layout/CmschPage'
import { l } from './language'

interface State {
  hasError: boolean
}

export class ErrorBoundary extends React.Component<PropsWithChildren, State> {
  public state: State = {
    hasError: false
  }

  static getDerivedStateFromError(_: Error) {
    return { hasError: true }
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error(errorInfo)
  }

  render() {
    if (this.state.hasError) {
      return (
        <CmschPage>
          <Helmet title="Hiba" />
          <Heading textAlign="center">{l('error-boundary-title')}</Heading>
          <Text textAlign="center" color="gray.500" marginTop={10}>
            {l('error-boundary-message')}
          </Text>
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
