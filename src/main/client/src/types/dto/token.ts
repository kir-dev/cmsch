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

export enum ScanViewState {
  Scanning,
  Loading,
  Error,
  Success
}

export interface ScanView {
  state: ScanViewState
  response?: ScanResponseDTO
  errorMessage?: string
}
