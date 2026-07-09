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
  joined: boolean
  joinCancellable: boolean
  participantCount: number
  participants: ParticipantView[]
  status: number
}

export type ParticipantView = {
  teamId: number
  teamName: string
}

export type SeededParticipantView = ParticipantView & {
  seed: number
}

export const TournamentJoinResponses = {
  OK: 'OK',
  ALREADY_JOINED: 'ALREADY_JOINED',
  TOURNAMENT_NOT_FOUND: 'TOURNAMENT_NOT_FOUND',
  NOT_JOINABLE: 'NOT_JOINABLE',
  INSUFFICIENT_PERMISSIONS: 'INSUFFICIENT_PERMISSIONS',
  ERROR: 'ERROR'
}
export type TournamentJoinResponses = (typeof TournamentJoinResponses)[keyof typeof TournamentJoinResponses]

export const TournamentJoinResponseMessages: Record<TournamentJoinResponses, string> = {
  [TournamentJoinResponses.OK]: 'Sikeresen csatlakoztál a versenyhez.',
  [TournamentJoinResponses.ALREADY_JOINED]: 'Már csatlakoztál ehhez a versenyhez.',
  [TournamentJoinResponses.TOURNAMENT_NOT_FOUND]: 'A verseny nem található.',
  [TournamentJoinResponses.NOT_JOINABLE]: 'A versenyhez nem lehet csatlakozni: lejárt a határidő, le van tiltva vagy betelt.',
  [TournamentJoinResponses.INSUFFICIENT_PERMISSIONS]: 'Nincs elég jogosultságod ehhez a művelethez.',
  [TournamentJoinResponses.ERROR]: 'Hiba történt a művelet végrehajtása során.'
}

export const TournamentCancelResponses = {
  OK: 'OK',
  NOT_PLAYING: 'NOT_PLAYING',
  TOURNAMENT_NOT_FOUND: 'TOURNAMENT_NOT_FOUND',
  NOT_CANCELABLE: 'NOT_CANCELABLE',
  INSUFFICIENT_PERMISSIONS: 'INSUFFICIENT_PERMISSIONS',
  ERROR: 'ERROR'
}
export type TournamentCancelResponses = (typeof TournamentCancelResponses)[keyof typeof TournamentCancelResponses]

export const TournamentCancelResponseMessages: Record<TournamentCancelResponses, string> = {
  [TournamentCancelResponses.OK]: 'Sikeresen visszavontad a jelentkezésed a versenyről.',
  [TournamentCancelResponses.NOT_PLAYING]: 'Nem vagy résztvevője ennek a versenynek.',
  [TournamentCancelResponses.TOURNAMENT_NOT_FOUND]: 'A verseny nem található.',
  [TournamentCancelResponses.NOT_CANCELABLE]: 'A jelentkezésedet nem lehet visszavonni: lejárt a határidő.',
  [TournamentCancelResponses.INSUFFICIENT_PERMISSIONS]: 'Nincs elég jogosultságod ehhez a művelethez.',
  [TournamentCancelResponses.ERROR]: 'Hiba történt a művelet végrehajtása során.'
}

export const StageStatus = {
  CREATED: 'CREATED',
  DRAFT: 'DRAFT',
  SET: 'SET',
  ONGOING: 'ONGOING',
  FINISHED: 'FINISHED',
  CANCELLED: 'CANCELLED'
}
export type StageStatus = (typeof StageStatus)[keyof typeof StageStatus]

export const MatchStatus = {
  NOT_STARTED: 'NOT_STARTED',
  IN_PROGRESS: 'IN_PROGRESS',
  CANCELLED: 'CANCELLED',
  COMPLETED: 'COMPLETED',
  BYE: 'BYE'
}
export type MatchStatus = (typeof MatchStatus)[keyof typeof MatchStatus]

export const StageType = {
  KNOCKOUT: 'KNOCKOUT',
  STAGE: 'STAGE'
}
export type StageType = (typeof StageType)[keyof typeof StageType]

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
  type: StageType
  name: string
  level: number
  participantCount: number
  participants: SeededParticipantView[]
  nextRound: number
  groupCount: number
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
