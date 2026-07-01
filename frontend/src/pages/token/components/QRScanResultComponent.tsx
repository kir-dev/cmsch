import Markdown from '@/common-components/Markdown'
import { ScanMessages, type ScanResponseView, ScanStatus } from '@/util/views/token.view'
import { AlertOctagon, AlertTriangle, CheckCircle2, Info, XCircle } from 'lucide-react'

interface QrScanResultProps {
  isError?: boolean
  response?: ScanResponseView
}

export const QRScanResultComponent = ({ response, isError }: QrScanResultProps) => {
  const renderIcon = () => {
    if (!response?.status || isError) {
      return <XCircle className="text-danger h-[120px] w-[120px]" />
    } else
      switch (response.status) {
        case ScanStatus.SCANNED:
          return <CheckCircle2 className="text-success h-[120px] w-[120px]" />
        case ScanStatus.ALREADY_SCANNED:
          return <Info className="text-info h-[120px] w-[120px]" />
        case ScanStatus.WRONG:
          return <AlertTriangle className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.CANNOT_COLLECT:
          return <AlertTriangle className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.INACTIVE:
          return <AlertTriangle className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.QR_FIGHT_LEVEL_LOCKED:
          return <Info className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.QR_FIGHT_LEVEL_NOT_OPEN:
          return <Info className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.QR_FIGHT_TOWER_LOCKED:
          return <Info className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.QR_TOWER_CAPTURED:
          return <CheckCircle2 className="text-success h-[120px] w-[120px]" />
        case ScanStatus.QR_TOWER_LOGGED:
          return <CheckCircle2 className="text-success h-[120px] w-[120px]" />
        case ScanStatus.QR_TOWER_ENSLAVED:
          return <CheckCircle2 className="text-success h-[120px] w-[120px]" />
        case ScanStatus.QR_TOWER_ALREADY_ENSLAVED:
          return <Info className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.QR_TOTEM_LOGGED:
          return <CheckCircle2 className="text-success h-[120px] w-[120px]" />
        case ScanStatus.QR_TOTEM_ENSLAVED:
          return <CheckCircle2 className="text-success h-[120px] w-[120px]" />
        case ScanStatus.QR_TOTEM_ALREADY_ENSLAVED:
          return <Info className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.QR_FIGHT_TOTEM_LOCKED:
          return <Info className="text-warning h-[120px] w-[120px]" />
        case ScanStatus.QR_TOWER_DAILY_LIMIT_EXCEEDED:
          return <AlertTriangle className="text-warning h-[120px] w-[120px]" />
        default:
          return <AlertOctagon className="text-danger h-[120px] w-[120px]" />
      }
  }

  return (
    <div className="text-center max-w-sm mx-auto">
      <div className="flex flex-col items-center p-10 mt-4">
        {response?.iconUrl ? <img src={response.iconUrl} alt={response.title} /> : renderIcon()}
        {response?.title && <h2 className="text-2xl font-bold mt-4">{response.title}</h2>}
        {!response?.title && isError && <h2 className="text-2xl font-bold mt-4">Hiba</h2>}
        {response && <p className="text-lg mt-2">{ScanMessages[response.status] || 'Ismeretlen eredmény'}</p>}
        {!response && isError && <p className="text-lg mt-2">Hiba a validálás során!</p>}
        {response?.description && (
          <div className="py-4 w-full">
            <Markdown text={response.description} />
          </div>
        )}
      </div>
    </div>
  )
}
