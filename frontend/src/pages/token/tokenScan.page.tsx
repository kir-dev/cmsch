import { ArrowLeft, Loader2, QrCode } from 'lucide-react'
import { useNavigate } from 'react-router'

import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { useScanTokenMutation } from '@/api/hooks/token/useScanTokenMutation'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { QrReader } from '@/common-components/QrReader'
import { Button } from '@/components/ui/button'
import { AbsolutePaths } from '@/util/paths'
import { useEffect } from 'react'
import { QRScanResultComponent } from './components/QRScanResultComponent'

const TokenScan = () => {
  const navigate = useNavigate()
  const { isLoggedIn } = useAuthContext()
  const { mutate, isPending, isError, reset, data, isIdle } = useScanTokenMutation()

  const handleScan = (qrData: string | null) => {
    if (qrData) {
      mutate(qrData)
    }
  }

  useEffect(() => {
    if (!isLoggedIn) return
    const params = new URLSearchParams(location.search)
    if (params.has('token')) {
      mutate(location.href)
    }
  }, [isLoggedIn, mutate])

  return (
    <CmschPage loginRequired={true} title="QR beolvasás">
      <h2 className="text-3xl font-bold mb-5 font-heading">QR beolvasás</h2>
      {isPending && <Loader2 className="h-12 w-12 animate-spin mb-5 text-primary" />}
      {isIdle && <QrReader onScan={handleScan} />}

      {!isIdle && (
        <div className="animate-in fade-in duration-500">
          <QRScanResultComponent response={data} isError={isError} />
        </div>
      )}

      <div className="flex flex-row space-x-2 mt-10">
        <Button
          variant="outline"
          className="flex items-center gap-2"
          onClick={() => {
            navigate(AbsolutePaths.TOKEN)
          }}
        >
          <ArrowLeft className="h-4 w-4" /> Vissza
        </Button>
        {!isIdle && (
          <Button className="flex items-center gap-2" onClick={reset}>
            <QrCode className="h-4 w-4" /> Új QR scannelése
          </Button>
        )}
      </div>
    </CmschPage>
  )
}

export default TokenScan
