import { CheckCircleIcon } from '@chakra-ui/icons'
import { Alert, AlertDescription, AlertIcon, AlertTitle, Center, CircularProgress, CloseButton, Fade, Heading } from '@chakra-ui/react'
import { Page } from 'components/@layout/Page'
import React from 'react'
import QRreader from 'react-qr-reader'
import { ScanView, ScanViewState } from 'types/dto/token'

export const QRScan: React.FC = (props) => {
  const [state, setState] = React.useState<ScanView>({ state: ScanViewState.Scanning })

  const handleScan = (data: any) => {
    if (data) {
      console.log(data)
      setState({ state: ScanViewState.Loading })
      setTimeout(() => {
        setState({ state: ScanViewState.Success })
      }, 1500)
    }
  }
  const handleError = (err: any) => {
    console.log(err)
  }

  return (
    <Page {...props}>
      <Heading>Scanneld be a QR kódot</Heading>
      {state.state == ScanViewState.Scanning && <QRreader delay={300} onError={handleError} onScan={handleScan}></QRreader>}
      {state.state == ScanViewState.Error && (
        <Alert status="error">
          <AlertIcon />
          <AlertTitle mr={2}>Valami hibát dobott</AlertTitle>
          <AlertDescription>{state.errorMessage ? state.errorMessage : 'Próbáld újra'}</AlertDescription>
          <CloseButton position="absolute" right="8px" top="8px" />
        </Alert>
      )}
      {state.state == ScanViewState.Loading && (
        <Center>
          <CircularProgress isIndeterminate color="brand.300" size="120px" />
        </Center>
      )}

      <Fade in={state.state == ScanViewState.Success}>
        <Center p="40px" mt="4">
          <CheckCircleIcon color="brand.500" boxSize="120px" />
        </Center>
        <Center>
          <Heading>Állomás lepecsételve</Heading>
        </Center>
      </Fade>
    </Page>
  )
}
