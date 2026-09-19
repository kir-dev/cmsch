export type BountyView = {
  rounds: BountyRoundView[]
  myRegistrationQr: string | null
  myGroupId: number | null
}

export type BountyPhase = 'BEFORE_REGISTRATION' | 'REGISTRATION' | 'BEFORE_GAME' | 'ACTIVE' | 'FINISHED'

export type BountyRoundView = {
  id: number
  name: string
  difficulty: 'EASY' | 'MEDIUM' | 'HARD'
  registrationStart: number
  registrationEnd: number
  gameStart: number
  gameEnd: number
  phase: BountyPhase
  finalized: boolean
  winnerGroupName: string | null
  myRegistration: BountyRegistrationView | null
  myTeam: BountyTeamView | null
}

export type BountyRegistrationView = {
  alive: boolean
  code: string | null
  deadline: number | null
  eliminatedAt: number | null
  eliminatedByInactivity: boolean
}

export type BountyTeamView = {
  groupName: string
  aliveCount: number
  targetGroupName: string | null
  targetAliveCount: number | null
  weapon: string | null
  winner: boolean
  rank: number | null
  killPoints: number
  survivalPoints: number | null
}

export type BountyKillResponse = {
  success: boolean
  message: string
  points: number | null
}
