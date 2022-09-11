export type QrArea = {
  id?: string
  name?: string
  status?: 'completed' | 'current' | 'unavailable'
  level: number
  unlocked: boolean
  teams: QrTeamStat[]
}

type QrTeamStat = {
  name: string
  value: number
}
