export interface TokenView {
  title: string
  type: string
}

export enum ScanStatus {
  SCANNED = 'SCANNED',
  ALREADY_SCANNED = 'ALREADY_SCANNED',
  WRONG = 'WRONG',
  CANNOT_COLLECT = 'CANNOT_COLLECT'
}

export interface ScanResponseView {
  title?: string
  status: ScanStatus
}
