import { Button, ButtonGroup, Fade, Heading } from '@chakra-ui/react'
import axios from 'axios'
import { LinkButton } from 'components/@commons/LinkButton'
import { QRScanResultComponent } from 'components/@commons/QRScanResultComponent'
import { Page } from 'components/@layout/Page'
import React from 'react'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import QRreader from 'react-qr-reader'
import { ScanResponseDTO, ScanStatus } from 'types/dto/token'
import { API_BASE_URL } from 'utils/configurations'
import { Loading } from 'utils/Loading'
import { useServiceContext } from '../../utils/useServiceContext'

enum ScanViewState {
  Scanning,
  Loading,
  Success
}

interface ScanView {
  state: ScanViewState
  response?: ScanResponseDTO
}

export const QRScan: React.FC = (props) => {
  const [state, setState] = React.useState<ScanView>({ state: ScanViewState.Scanning })
  const { throwError } = useServiceContext()
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
        .catch(() => {
          throwError('Hálózati hiba a token érvényesítésénél', { toast: true })
          // wait a few moments, so users wont spam error messages.
          setTimeout(() => {
            setState({ state: ScanViewState.Scanning })
          }, 750)
        })
    }
  }
  //handle any scanner error
  const handleError = (err: any) => {
    console.log(err)
    throwError('Beolvasási hiba.', { toast: true })
  }

  const resetButtonHandler = () => {
    setState({ state: ScanViewState.Scanning, response: undefined })
  }

  if (state.state == ScanViewState.Loading) return <Loading timeout={0} />

  return (
    <Page {...props} loginRequired>
      <Heading>Scanneld be a QR kódot</Heading>
      {state.state == ScanViewState.Scanning && <QRreader delay={300} onError={handleError} onScan={handleScan} />}

      {state.state == ScanViewState.Success && (
        <Fade in={state.state == ScanViewState.Success}>
          <QRScanResultComponent response={state.response || { status: ScanStatus.WRONG }} />
        </Fade>
      )}

      <ButtonGroup alignSelf="center" mt="5">
        <LinkButton leftIcon={<FaArrowLeft />} size="lg" href="/qr">
          Vissza
        </LinkButton>
        {state.state !== ScanViewState.Scanning && (
          <Button colorScheme="brand" leftIcon={<FaQrcode />} onClick={resetButtonHandler} size="lg">
            Új QR scannelése
          </Button>
        )}
      </ButtonGroup>
    </Page>
  )
}
