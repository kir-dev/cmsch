import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import { AbsolutePaths } from '@/util/paths'
import { type ScanResponseView, ScanStatus } from '@/util/views/token.view'
import { ArrowLeft, QrCode } from 'lucide-react'
import { useSearchParams } from 'react-router'
import { QRScanResultComponent } from './components/QRScanResultComponent'

const TokenScanResult = () => {
  const [searchParams] = useSearchParams()

  const server_response: ScanResponseView = {
    title: searchParams.get('title') || undefined,
    status: (searchParams.get('status') || ScanStatus.WRONG) as ScanStatus
  }
  return (
    <CmschPage title="QR eredmény">
      <QRScanResultComponent response={server_response} />
      <div className="flex flex-row space-x-6 justify-center mt-6">
        <LinkButton variant="outline" className="flex items-center gap-2" href={AbsolutePaths.TOKEN}>
          <ArrowLeft className="h-4 w-4" /> Vissza
        </LinkButton>
        <LinkButton className="flex items-center gap-2" href={`${AbsolutePaths.TOKEN}/scan`}>
          <QrCode className="h-4 w-4" /> Új QR-kód scannelése
        </LinkButton>
      </div>
    </CmschPage>
  )
}

export default TokenScanResult
