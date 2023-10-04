import { TaskCategoryPreview } from './task.view'

export type TeamDashboardView = {
  memberCount: number
  points: number
  leaderboardPlace: number
  teamName: string
  bannerUrl: string
  creatorName: string
  teamId: string
  riddles: {
    maxCount: number
    solvedCount: number
  }
  task: {
    categories: TaskCategoryPreview[]
  }
}

export type TeamView = {
  coverUrl: string
  description: string
  leaderNotes: string
  points: number
  members: TeamMemberView[]
  applicants: TeamMemberView[]
  joinEnabled: boolean
  leaveEnabled: boolean
  joinCancellable: boolean
  ownTeam: boolean
  stats: TeamStatView[]
  taskCategories?: TeamTaskCategoriesView[]
  forms?: TeamFormView[]
  logo?: string
} & TeamListItemView

export type TeamStatView = { name: string; value1: string; value2?: string; navigate?: string; percentage?: number }

export type TeamListItemView = {
  id: number
  name: string
}

export type TeamMemberView = {
  name: string
  id: number
  admin: boolean
  you: boolean
}

export type TeamTaskCategoriesView = {
  name: string
  completed: number
  outOf: number
  id: number | null | undefined
  navigate: number | null | undefined
}

export type TeamFormView = {
  name: string
  filled: boolean
  availableUntil: number
  url: string
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
  [TeamResponses.OK_RELOG_REQUIRED]: 'Sikeres, újbóli bejelentkezés szükséges.',
  [TeamResponses.LEAVE_DISABLED]: 'Távozás letiltva.',
  [TeamResponses.ERROR]: 'Sikertelen művelet!'
}

export interface CreateTeamDto {
  name: string
}

export interface TeamEditDto {
  description: string
  logo?: File
}
