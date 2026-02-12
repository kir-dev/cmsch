import { ButtonGroup } from '@chakra-ui/react'
import { FaArrowLeft, FaQrcode } from 'react-icons/fa'
import { useSearchParams } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../util/paths'
import { type ScanResponseView, ScanStatus } from '../../util/views/token.view'
import { QRScanResultComponent } from './components/QRScanResultComponent'

const TokenScanResult = () => {
  const [searchParams] = useSearchParams()
  const brandColor = useBrandColor()
  const app = useConfigContext()?.components?.app

  const server_response: ScanResponseView = {
    title: searchParams.get('title') || undefined,
    status: (searchParams.get('status') || ScanStatus.WRONG) as ScanStatus
  }
  return (
    <CmschPage>
      <title>{app?.siteName || 'CMSch'} | QR eredmény</title>
      <QRScanResultComponent response={server_response} />
      <ButtonGroup spacing="6" alignSelf="center">
        <LinkButton leftIcon={<FaArrowLeft />} href={AbsolutePaths.TOKEN}>
          Vissza
        </LinkButton>
        <LinkButton colorScheme={brandColor} leftIcon={<FaQrcode />} href={`${AbsolutePaths.TOKEN}/scan`}>
          Új QR-kód scannelése
        </LinkButton>
      </ButtonGroup>
    </CmschPage>
  )
}

export default TokenScanResult
