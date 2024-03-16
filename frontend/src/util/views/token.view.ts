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
  INACTIVE = 'INACTIVE',
  QR_FIGHT_LEVEL_NOT_OPEN = 'QR_FIGHT_LEVEL_NOT_OPEN',
  QR_FIGHT_LEVEL_LOCKED = 'QR_FIGHT_LEVEL_LOCKED',
  QR_FIGHT_TOWER_LOCKED = 'QR_FIGHT_TOWER_LOCKED',
  QR_FIGHT_INTERNAL_ERROR = 'QR_FIGHT_INTERNAL_ERROR',
  QR_TOWER_LOGGED = 'QR_TOWER_LOGGED',
  QR_TOWER_CAPTURED = 'QR_TOWER_CAPTURED',
  QR_TOWER_ENSLAVED = 'QR_TOWER_ENSLAVED',
  QR_TOWER_ALREADY_ENSLAVED = 'QR_TOWER_ALREADY_ENSLAVED',
  QR_TOTEM_LOGGED = 'QR_TOTEM_LOGGED',
  QR_TOTEM_ENSLAVED = 'QR_TOTEM_ENSLAVED',
  QR_TOTEM_ALREADY_ENSLAVED = 'QR_TOTEM_ALREADY_ENSLAVED',
  QR_FIGHT_TOTEM_LOCKED = 'QR_FIGHT_TOTEM_LOCKED'
}

export const ScanMessages: Record<ScanStatus, string> = {
  [ScanStatus.SCANNED]: 'QR Beolvasva',
  [ScanStatus.ALREADY_SCANNED]: 'Már beolvasott QR',
  [ScanStatus.WRONG]: 'Rossz QR',
  [ScanStatus.CANNOT_COLLECT]: 'Nem lehet megszerezni ezt a QR-t',
  [ScanStatus.INACTIVE]: 'Ez a QR jelenleg nem aktív',
  [ScanStatus.QR_FIGHT_LEVEL_NOT_OPEN]: 'Ez a QR harc szint még nincs megnyitva',
  [ScanStatus.QR_FIGHT_LEVEL_LOCKED]: 'Ez a QR harc szint zárva van',
  [ScanStatus.QR_FIGHT_TOWER_LOCKED]: 'Ez a QR harc torony zárva van',
  [ScanStatus.QR_FIGHT_INTERNAL_ERROR]: 'QR harc belső hiba',
  [ScanStatus.QR_TOWER_LOGGED]: 'QR torony megjelölve',
  [ScanStatus.QR_TOWER_CAPTURED]: 'QR torony elfoglalva',
  [ScanStatus.QR_TOTEM_LOGGED]: 'QR totem megjelölve',
  [ScanStatus.QR_TOWER_ENSLAVED]: 'QR torony leigázva', // bocsi
  [ScanStatus.QR_TOWER_ALREADY_ENSLAVED]: 'Ez a QR torony már le van igázva',
  [ScanStatus.QR_TOWER_ALREADY_ENSLAVED]: 'QR totem megjelölve',
  [ScanStatus.QR_TOTEM_ENSLAVED]: 'QR totem elfoglalva',
  [ScanStatus.QR_TOTEM_ALREADY_ENSLAVED]: 'Ez a QR totem már el van foglalva',
  [ScanStatus.QR_FIGHT_TOTEM_LOCKED]: 'QR totem zárva'
}

export interface ScanResponseView {
  title?: string
  description?: string
  iconUrl?: string
  status: ScanStatus
}
