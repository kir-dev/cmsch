export interface TokenProgress {
  totalTokenCount: number
  collectedTokenCount: number
  minTokenToComplete: number
  tokens: TokenView[]
}

export interface TokenView {
  title: string
  type: string
  icon: string
}

export enum ScanStatus {
  SCANNED = 'SCANNED',
  ALREADY_SCANNED = 'ALREADY_SCANNED',
  WRONG = 'WRONG',
  CANNOT_COLLECT = 'CANNOT_COLLECT',
  QR_FIGHT_LEVEL_NOT_OPEN = 'QR_FIGHT_LEVEL_NOT_OPEN',
  QR_FIGHT_LEVEL_LOCKED = 'QR_FIGHT_LEVEL_LOCKED',
  QR_FIGHT_TOWER_LOCKED = 'QR_FIGHT_TOWER_LOCKED',
  QR_FIGHT_INTERNAL_ERROR = 'QR_FIGHT_INTERNAL_ERROR',
  QR_TOWER_LOGGED = 'QR_TOWER_LOGGED',
  QR_TOWER_CAPTURED = 'QR_TOWER_CAPTURED'
}

export const ScanMessages: Record<ScanStatus, string> = {
  [ScanStatus.SCANNED]: 'QR Beolvasva',
  [ScanStatus.ALREADY_SCANNED]: 'Már beolvasott QR',
  [ScanStatus.WRONG]: 'Rossz QR',
  [ScanStatus.CANNOT_COLLECT]: 'Nem lehet megszerezni ezt a QR-t',
  [ScanStatus.QR_FIGHT_LEVEL_NOT_OPEN]: 'Ez QR harc szint még nincs megnyitva',
  [ScanStatus.QR_FIGHT_LEVEL_LOCKED]: 'Ez QR harc szint zárva van',
  [ScanStatus.QR_FIGHT_TOWER_LOCKED]: 'Ez QR harc torony zárva van',
  [ScanStatus.QR_FIGHT_INTERNAL_ERROR]: 'QR harc belső hiba',
  [ScanStatus.QR_TOWER_LOGGED]: 'QR torony megjelölve',
  [ScanStatus.QR_TOWER_CAPTURED]: 'QR torony elfoglalva'
}

export interface ScanResponseView {
  title?: string
  status: ScanStatus
}
