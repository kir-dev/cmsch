export interface TokenDTO {
  title: string
  type: string
}

export enum ScanStatus {
  SCANNED = 'SCANNED',
  ALREADY_SCANNED = 'ALREADY_SCANNED',
  WRONG = 'WRONG',
  CANNOT_COLLECT = 'CANNOT_COLLECT'
}

export interface ScanResponseDTO {
  title?: string
  status: ScanStatus
}
