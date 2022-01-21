import { Page } from '../@layout/Page'
import React from 'react'
import { ScanResponseDTO, ScanStatus } from 'types/dto/token'
import { QRScanResultComponent } from 'components/@commons/QRScanResultComponent'
import { useSearchParams } from 'react-router-dom'
import { ButtonGroup } from '@chakra-ui/react'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import { LinkButton } from '../@commons/LinkButton'
import { Helmet } from 'react-helmet'

export const QRScanResult: React.FC = (props) => {
  const [searchParams] = useSearchParams()
  const server_response: ScanResponseDTO = {
    title: searchParams.get('title') || undefined,
    status: (searchParams.get('status') || ScanStatus.WRONG) as ScanStatus
  }
  return (
    <Page {...props}>
      <Helmet title="QR eredmény" />
      <QRScanResultComponent response={server_response} />
      <ButtonGroup spacing="6" alignSelf="center">
        <LinkButton leftIcon={<FaArrowLeft />} href="/qr">
          Vissza
        </LinkButton>
        <LinkButton colorScheme="brand" leftIcon={<FaQrcode />} href="/qr/scan">
          Új QR-kód scannelése
        </LinkButton>
      </ButtonGroup>
    </Page>
  )
}
