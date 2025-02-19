export type TournamentPreview = {
  id: number
  title: string
  description: string
  location: string
  startDate: string
  endDate: string
  status: string
}

type TournamentWithParticipantsView = {
  id: number
  title: string
  description: string
  location: string
  participants: ParticipantView[]
}

type ParticipantView = {
  id: number
  name: string
}

enum StageStatus {
  CREATED= 'CREATED',
  DRAFT = 'DRAFT',
  SET = 'SET',
  ONGOING = 'ONGOING',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED',
}

enum MatchStatus {
  NOT_STARTED = 'NOT_STARTED',
  HT = 'HT',
  FT = 'FT',
  AET = 'AET',
  AP = 'AP',
  IN_PROGRESS = 'IN_PROGRESS',
  CANCELLED = 'CANCELLED',
}

type MatchView = {
  id: number
  gameId: number
  participant1: ParticipantView
  participant2: ParticipantView
  score1: number
  score2: number
  status: MatchStatus
}

type TournamentStageView = {
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

