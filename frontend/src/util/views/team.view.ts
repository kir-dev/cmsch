export type TeamView = {
  points: number
  members: TeamMemberView[]
  applicants: TeamMemberView[]
  joinEnabled: boolean
  leaveEnabled: boolean
} & TeamListItemView

export type TeamListItemView = {
  id: number
  name: string
}

export type TeamMemberView = {
  name: string
  id: number
  isAdmin: boolean
}

export enum TeamResponses {
  ALREADY_IN_GROUP,
  JOINING_DISABLED,
  NOT_JOINABLE,
  ALREADY_SUBMITTED_JOIN_REQUEST,
  OK,
  INVALID_NAME,
  USED_NAME,
  CREATION_DISABLED,
  INSUFFICIENT_PERMISSIONS,
  OK_RELOG_REQUIRED,
  LEAVE_DISABLED
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
  [TeamResponses.LEAVE_DISABLED]: 'Távozás letiltva.'
}

export interface CreateTeamDto {
  name: string
}

export interface TeamIdDto {
  id: number
}
