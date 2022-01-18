import { Page } from '../@layout/Page'
import React from 'react'
import { ScanResponseDTO, ScanStatus } from 'types/dto/token'
import { QRScanResultComponent } from 'components/@commons/QRScanResultComponent'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { ButtonGroup, Button } from '@chakra-ui/react'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'

export const QRScanResult: React.FC = (props) => {
  const [searchParams] = useSearchParams()
  const server_response: ScanResponseDTO = {
    title: searchParams.get('title') || undefined,
    status: (searchParams.get('status') || ScanStatus.WRONG) as ScanStatus
  }
  const navigate = useNavigate()

  const scanEventHandler = () => {
    navigate('/qr/scan')
  }
  const backButtonHandler = () => {
    navigate('/qr')
  }

  return (
    <Page {...props}>
      <QRScanResultComponent response={server_response} />
      <ButtonGroup spacing="6" alignSelf="center">
        <Button leftIcon={<FaArrowLeft />} onClick={backButtonHandler}>
          Vissza{' '}
        </Button>
        <Button colorScheme="brand" leftIcon={<FaQrcode />} onClick={scanEventHandler}>
          Új QR-kód scannelése
        </Button>
      </ButtonGroup>
    </Page>
  )
}
