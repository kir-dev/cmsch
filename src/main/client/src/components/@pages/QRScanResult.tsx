import { Page } from '../@layout/Page'
import React from 'react'
import { ScanResponseDTO, ScanStatus } from 'types/dto/token'
import { QrScanResultComponent } from 'components/@commons/QrScanResultComponent'
import { useSearchParams } from 'react-router-dom'

export const QRScanResult: React.FC = (props) => {
  const [searchParams] = useSearchParams()
  const server_response: ScanResponseDTO = {
    title: searchParams.get('title') || undefined,
    status: (searchParams.get('status') || ScanStatus.WRONG) as ScanStatus
  }
  return (
    <Page {...props}>
      <QrScanResultComponent response={server_response} />
    </Page>
  )
}
