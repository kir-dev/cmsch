import { ButtonGroup } from '@chakra-ui/react'
import { QRScanResultComponent } from 'components/@commons/QRScanResultComponent'
import { FC } from 'react'
import { Helmet } from 'react-helmet'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import { useSearchParams } from 'react-router-dom'
import { ScanResponseDTO, ScanStatus } from 'types/dto/token'
import { LinkButton } from '../@commons/LinkButton'
import { Page } from '../@layout/Page'

export const QRScanResult: FC = (props) => {
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
