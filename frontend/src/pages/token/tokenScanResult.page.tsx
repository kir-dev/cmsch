import { ButtonGroup } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import { useSearchParams } from 'react-router-dom'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { ScanResponseView, ScanStatus } from '../../util/views/token.view'
import { QRScanResultComponent } from './components/QRScanResultComponent'
import { LinkButton } from '../../common-components/LinkButton'
import { AbsolutePaths } from '../../util/paths'

const TokenScanResult = () => {
  const [searchParams] = useSearchParams()
  const server_response: ScanResponseView = {
    title: searchParams.get('title') || undefined,
    status: (searchParams.get('status') || ScanStatus.WRONG) as ScanStatus
  }
  return (
    <CmschPage>
      <Helmet title="QR eredmény" />
      <QRScanResultComponent response={server_response} />
      <ButtonGroup spacing="6" alignSelf="center">
        <LinkButton leftIcon={<FaArrowLeft />} href={AbsolutePaths.TOKEN}>
          Vissza
        </LinkButton>
        <LinkButton colorScheme="brand" leftIcon={<FaQrcode />} href={`${AbsolutePaths.TOKEN}/scan`}>
          Új QR-kód scannelése
        </LinkButton>
      </ButtonGroup>
    </CmschPage>
  )
}

export default TokenScanResult
