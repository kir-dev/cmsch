export type TournamentPreview = {
  id: number
  title: string
  description: string
  location: string
  status: number
}

export type TournamentWithParticipantsView = {
  id: number
  title: string
  description: string
  location: string
  participants: ParticipantView[]
}

export type ParticipantView = {
  id: number
  name: string
}

export enum StageStatus {
  CREATED= 'CREATED',
  DRAFT = 'DRAFT',
  SET = 'SET',
  ONGOING = 'ONGOING',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED',
}

export enum MatchStatus {
  NOT_STARTED = 'NOT_STARTED',
  HT = 'HT',
  FT = 'FT',
  AET = 'AET',
  AP = 'AP',
  IN_PROGRESS = 'IN_PROGRESS',
  CANCELLED = 'CANCELLED',
}

export type MatchView = {
  id: number
  gameId: number
  kickoffTime?: number
  level: number
  location: string
  seed1: number
  seed2: number
  participant1?: ParticipantView
  participant2?: ParticipantView
  score1?: number
  score2?: number
  status: MatchStatus
}

export type TournamentStageView = {
  id: number
  name: string
  level: number
  participantCount: number
  nextRound: number
  status: StageStatus
  matches: MatchView[]
}

export type TournamentDetailsView = {
  tournament: TournamentWithParticipantsView
  stages: TournamentStageView[]
}

