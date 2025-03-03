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
  teamId: number
  teamName: string
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
  IN_PROGRESS = 'IN_PROGRESS',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED',
}

export type MatchView = {
  id: number
  gameId: number
  kickoffTime?: number
  level: number
  location: string
  homeSeed: number
  awaySeed: number
  home?: ParticipantView
  away?: ParticipantView
  homeScore?: number
  awayScore?: number
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

