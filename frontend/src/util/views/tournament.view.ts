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
  joinEnabled: boolean
  isJoined: boolean
  participants: ParticipantView[]
  status: number
}

export type ParticipantView = {
  teamId: number
  teamName: string
}

export enum TournamentResponses {
  OK = 'OK',
  JOINING_DISABLED = 'JOINING_DISABLED',
  ALREADY_JOINED = 'ALREADY_JOINED',
  TOURNAMENT_NOT_FOUND = 'TOURNAMENT_NOT_FOUND',
  NOT_JOINABLE = 'NOT_JOINABLE',
  INSUFFICIENT_PERMISSIONS = 'INSUFFICIENT_PERMISSIONS',
  ERROR = 'ERROR'
}

export const TournamentResponseMessages: Record<TournamentResponses, string> = {
  [TournamentResponses.OK]: 'Sikeresen csatlakoztál a versenyhez.',
  [TournamentResponses.JOINING_DISABLED]: 'A versenyhez való csatlakozás jelenleg le van tiltva.',
  [TournamentResponses.ALREADY_JOINED]: 'Már csatlakoztál ehhez a versenyhez.',
  [TournamentResponses.TOURNAMENT_NOT_FOUND]: 'A verseny nem található.',
  [TournamentResponses.NOT_JOINABLE]: 'A versenyhez való csatlakozás nem lehetséges.',
  [TournamentResponses.INSUFFICIENT_PERMISSIONS]: 'Nincs elég jogosultságod ehhez a művelethez.',
  [TournamentResponses.ERROR]: 'Hiba történt a művelet végrehajtása során.'
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

export type OptionalTournamentView = {
  visible: boolean
  tournament?: TournamentDetailsView
}

