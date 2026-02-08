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
  GUEST: 0, // anyone without login
  BASIC: 1, // has auth.sch but not member of SSSL
  ATTENDEE: 10, // has auth.sch but not member of SSSL and has a group or has submitted form
  PRIVILEGED: 11, // has auth.sch but not member of SSSL and is a group admin
  STAFF: 100, // member of the SSSL
  ADMIN: 200, // the organizers of the event
  SUPERUSER: 500 // advanced user management (able to grant admin access)
} as const
export type RoleType = (typeof RoleType)[keyof typeof RoleType]

export const RoleTypeString = {
  GUEST: 'GUEST',
  BASIC: 'BASIC',
  ATTENDEE: 'ATTENDEE',
  PRIVILEGED: 'PRIVILEGED',
  STAFF: 'STAFF',
  ADMIN: 'ADMIN',
  SUPERUSER: 'SUPERUSER'
} as const
export type RoleTypeString = (typeof RoleTypeString)[keyof typeof RoleTypeString]

export const GuildType = {
  UNKNOWN: 'Nincs',
  RED: 'Piros',
  BLACK: 'Fekete',
  BLUE: 'Kék',
  WHITE: 'Fehér',
  YELLOW: 'Sárga',
  PURPLE: 'Lila'
} as const
export type GuildType = (typeof GuildType)[keyof typeof GuildType]

export const MajorType = {
  UNKNOWN: 0,
  IT: 1,
  EE: 2,
  BPROF: 3
} as const
export type MajorType = (typeof MajorType)[keyof typeof MajorType]
