export type QrDto = {
  mainLevels: QrLevelDto[]
  extraLevels: QrLevelDto[]
}

export type QrLevelDto = {
  name: string
  description: string
  status: LevelStatus
  owners: string
  teams: Record<string, number>
  towers: Tower[]
}

export type Tower = {
  name: string
  ownerNow?: string
  holder?: string
  holdingFor?: number
}

export enum LevelStatus {
  NOT_LOGGED_IN = 'NOT_LOGGED_IN',
  NOT_ENABLED = 'NOT_ENABLED',
  NOT_UNLOCKED = 'NOT_UNLOCKED',
  OPEN = 'OPEN',
  COMPLETED = 'COMPLETED'
}
