export type QrDto = {
  mainLevels: QrLevelDto[]
  extraLevels: QrLevelDto[]
}

export type QrLevelDto = {
  name: string
  description: string
  tokenCount: number
  status: LevelStatus
  owners: string
  teams: Record<string, number>
  towers: Tower[]
  totems: Totem[]
}

export type Tower = {
  name: string
  ownerNow?: string
  description?: string
  holder?: string
  holdingFor?: number
}

export type Totem = {
  name: string
  description?: string
  owner?: string
}

export enum LevelStatus {
  NOT_LOGGED_IN = 'NOT_LOGGED_IN',
  NOT_ENABLED = 'NOT_ENABLED',
  NOT_UNLOCKED = 'NOT_UNLOCKED',
  OPEN = 'OPEN',
  COMPLETED = 'COMPLETED'
}
