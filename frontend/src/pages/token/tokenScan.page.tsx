import { Button, ButtonGroup, Fade, Heading } from '@chakra-ui/react'
import axios from 'axios'
import { useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import QRreader from 'react-qr-reader'
import { ScanResponseView, ScanStatus } from '../../util/views/token.view'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import { QRScanResultComponent } from './components/QRScanResultComponent'
import { l } from '../../util/language'
import { useNavigate } from 'react-router-dom'

enum ScanViewState {
  Scanning,
  Loading,
  Success
}

interface ScanView {
  state: ScanViewState
  response?: ScanResponseView
}

const TokenScan = () => {
  const [state, setState] = useState<ScanView>({ state: ScanViewState.Scanning })
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
        .post(`/api/token/${token}`)
        .then((res) => {
          setState({ state: ScanViewState.Success, response: res.data })
        })
        .catch(() => {
          console.error(l('token-scan-network-error'), { toast: true })
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
    console.error(l('token-scan-read-error'), { toast: true })
  }

  const resetButtonHandler = () => {
    setState({ state: ScanViewState.Scanning, response: undefined })
  }

  if (state.state == ScanViewState.Loading) return <Loading timeout={0} />

  return (
    <CmschPage loginRequired>
      <Helmet title="QR beolvasás" />
      <Heading mb={5}>QR beolvasás</Heading>
      {state.state == ScanViewState.Scanning && <QRreader delay={300} onError={handleError} onScan={handleScan} />}

      {state.state == ScanViewState.Success && (
        <Fade in={state.state == ScanViewState.Success}>
          <QRScanResultComponent response={state.response || { status: ScanStatus.WRONG }} />
        </Fade>
      )}

      <ButtonGroup alignSelf="center" mt={10}>
        <Button
          leftIcon={<FaArrowLeft />}
          size="lg"
          onClick={() => {
            navigate(-1)
          }}
        >
          Vissza
        </Button>
        {state.state !== ScanViewState.Scanning && (
          <Button colorScheme="brand" leftIcon={<FaQrcode />} onClick={resetButtonHandler} size="lg">
            Új QR scannelése
          </Button>
        )}
      </ButtonGroup>
    </CmschPage>
  )
}

export default TokenScan
