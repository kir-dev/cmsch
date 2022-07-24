import { Button, ButtonGroup, Fade, Heading } from '@chakra-ui/react'
import axios from 'axios'
import { useState } from 'react'
import { Helmet } from 'react-helmet'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import QRreader from 'react-qr-reader'
import { ScanResponseView, ScanStatus } from '../../util/views/token.view'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import { QRScanResultComponent } from './components/QRScanResultComponent'
import { LinkButton } from '../../common-components/LinkButton'
import { AbsolutePaths } from '../../util/paths'

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
          console.error('Hálózati hiba a token érvényesítésénél', { toast: true })
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
    console.error('Beolvasási hiba.', { toast: true })
  }

  const resetButtonHandler = () => {
    setState({ state: ScanViewState.Scanning, response: undefined })
  }

  if (state.state == ScanViewState.Loading) return <Loading timeout={0} />

  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title="QR beolvasás" />
      <Heading>Scannelje be a QR kódot</Heading>
      {state.state == ScanViewState.Scanning && <QRreader delay={300} onError={handleError} onScan={handleScan} />}

      {state.state == ScanViewState.Success && (
        <Fade in={state.state == ScanViewState.Success}>
          <QRScanResultComponent response={state.response || { status: ScanStatus.WRONG }} />
        </Fade>
      )}

      <ButtonGroup alignSelf="center" mt="5">
        <LinkButton leftIcon={<FaArrowLeft />} size="lg" href={AbsolutePaths.TOKEN}>
          Vissza
        </LinkButton>
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
