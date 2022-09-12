export type TeamView = {
  points: number
  members: TeamMemberView[]
  applicants: TeamMemberView[]
  joinEnabled: boolean
  leaveEnabled: boolean
  joinCancellable: boolean
  ownTeam: boolean
} & TeamListItemView

export type TeamListItemView = {
  id: number
  name: string
}

export type TeamMemberView = {
  name: string
  id: number
  admin: boolean
}

export enum TeamResponses {
  ALREADY_IN_GROUP = 'ALREADY_IN_GROUP',
  JOINING_DISABLED = 'JOINING_DISABLED',
  NOT_JOINABLE = 'NOT_JOINABLE',
  ALREADY_SUBMITTED_JOIN_REQUEST = 'ALREADY_SUBMITTED_JOIN_REQUEST',
  OK = 'OK',
  INVALID_NAME = 'INVALID_NAME',
  USED_NAME = 'USED_NAME',
  CREATION_DISABLED = 'CREATION_DISABLED',
  INSUFFICIENT_PERMISSIONS = 'INSUFFICIENT_PERMISSIONS',
  OK_RELOG_REQUIRED = 'OK_RELOG_REQUIRED',
  LEAVE_DISABLED = 'LEAVE_DISABLED',
  ERROR = 'ERROR'
}

export const TeamResponseMessages: Record<TeamResponses, string> = {
  [TeamResponses.OK]: 'Sikeres művelet!',
  [TeamResponses.ALREADY_IN_GROUP]: 'Már egy csapat tagja vagy.',
  [TeamResponses.ALREADY_SUBMITTED_JOIN_REQUEST]: 'Már jelentkeztél egy csapatba.',
  [TeamResponses.NOT_JOINABLE]: 'Nem lehet csatlakozni.',
  [TeamResponses.JOINING_DISABLED]: 'Csatlakozás letiltva.',
  [TeamResponses.INVALID_NAME]: 'Érvénytelen név.',
  [TeamResponses.USED_NAME]: 'Név már használatban.',
  [TeamResponses.CREATION_DISABLED]: 'Létrehozás letiltva.',
  [TeamResponses.INSUFFICIENT_PERMISSIONS]: 'Nincs jogosultságod.',
  [TeamResponses.OK_RELOG_REQUIRED]: 'Sikeres, újboli bejelentkezés szükséges.',
  [TeamResponses.LEAVE_DISABLED]: 'Távozás letiltva.',
  [TeamResponses.ERROR]: 'Sikertelen művelet!'
}

export interface CreateTeamDto {
  name: string
}
