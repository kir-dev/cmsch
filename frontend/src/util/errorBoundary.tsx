import React, { ErrorInfo } from 'react'
import { HasChildren } from './react-types.util'
import { Helmet } from 'react-helmet-async'
import { ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { LinkButton } from '../common-components/LinkButton'
import { CmschPage } from '../common-components/layout/CmschPage'

interface State {
  hasError: boolean
}

export class ErrorBoundary extends React.Component<HasChildren, State> {
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
          <Heading textAlign="center">Hiba történt</Heading>
          <Text textAlign="center" color="gray.500" marginTop={10}>
            Sajnos ilyennel még nem találkoztunk. Légyszíves ezt jelezd a fejlesztőknek!
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
