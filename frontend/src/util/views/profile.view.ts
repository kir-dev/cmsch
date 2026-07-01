import type { DebtView } from './debt.view'
import type { GroupLeaderView } from './groupLeader.view'
import type { GroupMemberLocationView } from './groupMemberLocation.view'
import type { TokenView } from './token.view'
import type { TopListAbstractEntryView } from './toplistAbstractEntry.view'

export interface ProfileView {
  cmschId?: string
  loggedIn: boolean
  fullName?: string
  alias?: string
  email?: string
  neptun?: string
  guild?: keyof typeof GuildType
  major?: MajorType

  groupName?: string
  groupLeaders?: GroupLeaderView[]
  groupSelectionAllowed: boolean
  availableGroups?: Record<number, string>
  fallbackGroup?: number

  role: keyof typeof RoleType

  tokens?: TokenView[]
  minTokenToComplete?: number
  totalTokenCount?: number
  collectedTokenCount?: number

  totalRiddleCount?: number
  completedRiddleCount?: number

  totalTaskCount?: number
  submittedTaskCount?: number
  completedTaskCount?: number

  raceStats?: RaceStatsView

  locations?: GroupMemberLocationView[]
  debts?: DebtView[]
  leaderboard?: TopListAbstractEntryView[]

  profileIsComplete?: boolean
  incompleteTasks?: string[]

  groupMessage?: string
  userMessage?: string
}

export interface RaceStatsView {
  bestTime: number
  placement: number
  timesParticipated: number
  averageTime: number
  deviation: number
  kCaloriesPerSecond: number
}

//cannot compare roles if the enums values are strings use the RoleType[role] syntax
export const RoleType = {
  GUEST: 0,
  BASIC: 1,
  ATTENDEE: 10,
  PRIVILEGED: 11,
  STAFF: 100,
  ADMIN: 200,
  SUPERUSER: 500
}
export type RoleType = (typeof RoleType)[keyof typeof RoleType]
export type RoleTypeString = keyof typeof RoleType

export const RoleTypeString: { [k: string]: RoleTypeString } = {
  GUEST: 'GUEST',
  BASIC: 'BASIC',
  ATTENDEE: 'ATTENDEE',
  PRIVILEGED: 'PRIVILEGED',
  STAFF: 'STAFF',
  ADMIN: 'ADMIN',
  SUPERUSER: 'SUPERUSER'
}

export const GuildType = {
  UNKNOWN: 'Nincs',
  RED: 'Piros',
  BLACK: 'Fekete',
  BLUE: 'Kék',
  WHITE: 'Fehér',
  YELLOW: 'Sárga',
  PURPLE: 'Lila'
}
export type GuildType = (typeof GuildType)[keyof typeof GuildType]

export const MajorType = {
  UNKNOWN: 'UNKNOWN',
  IT: 'IT',
  EE: 'EE',
  BPROF: 'BPROF'
}
export type MajorType = (typeof MajorType)[keyof typeof MajorType]
