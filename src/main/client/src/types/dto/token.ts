export interface TokenDTO {
  title: string
  type: string
}

export enum ScanStatus {
  SCANNED,
  ALREADY_SCANNED,
  WRONG
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
