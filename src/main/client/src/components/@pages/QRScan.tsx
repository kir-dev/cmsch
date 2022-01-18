import {
  Alert,
  AlertDescription,
  AlertIcon,
  AlertTitle,
  Button,
  ButtonGroup,
  Center,
  CircularProgress,
  CloseButton,
  Fade,
  Heading
} from '@chakra-ui/react'
import axios from 'axios'
import { QRScanResultComponent } from 'components/@commons/QRScanResultComponent'
import { Page } from 'components/@layout/Page'
import React from 'react'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import QRreader from 'react-qr-reader'
import { useNavigate } from 'react-router-dom'
import { ScanResponseDTO, ScanStatus } from 'types/dto/token'
import { API_BASE_URL } from 'utils/configurations'

enum ScanViewState {
  Scanning,
  Loading,
  Error,
  Success
}

interface ScanView {
  state: ScanViewState
  response?: ScanResponseDTO
  errorMessage?: string
}

export const QRScan: React.FC = (props) => {
  const [state, setState] = React.useState<ScanView>({ state: ScanViewState.Scanning })
  const navigate = useNavigate()

  const handleScan = (qrData: any) => {
    if (qrData) {
      // set state to loading
      setState({ state: ScanViewState.Loading })

      //get token id from scanned url
      const token = (qrData as string)
        .split('/')
        .filter((part) => part !== '')
        .pop()

      //send token to backaend with post
      axios
        .post(`${API_BASE_URL}/api/token/${token}`)
        .then((res) => {
          setState({ state: ScanViewState.Success, response: res.data })
        })
        //catch any network error
        .catch((err) => {
          console.log(err)
          setState({ state: ScanViewState.Error, errorMessage: 'A szerver nem válaszol!' })
        })
    }
  }
  //handle any scanner error
  const handleError = (err: any) => {
    console.log(err)
  }

  const backButtonHandler = () => {
    navigate('/qr')
  }
  const resetButtonHandler = () => {
    setState({ state: ScanViewState.Scanning, response: undefined, errorMessage: '' })
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
      {state.state == ScanViewState.Success && (
        <Fade in={state.state == ScanViewState.Success}>
          <QRScanResultComponent response={state.response || { status: ScanStatus.WRONG }}></QRScanResultComponent>
        </Fade>
      )}
      {state.state !== ScanViewState.Loading && (
        <ButtonGroup alignSelf="center">
          <Button marginTop="3" leftIcon={<FaArrowLeft />} onClick={backButtonHandler} size="lg">
            Vissza
          </Button>
          {state.state !== ScanViewState.Scanning && (
            <Button marginTop="3" colorScheme="brand" leftIcon={<FaQrcode />} onClick={resetButtonHandler} size="lg">
              Új QR scannelése
            </Button>
          )}
        </ButtonGroup>
      )}
    </Page>
  )
}
