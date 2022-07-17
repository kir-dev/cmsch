import { DebtView } from './debt.view'
import { GroupLeaderView } from './groupLeader.view'
import { GroupMemberLocationView } from './groupMemberLocation.view'
import { TokenView } from './token.view'
import { TopListAbstractEntryView } from './toplistAbstractEntry.view'

export interface ProfileView {
  cmschId: string
  loggedIn: boolean
  fullName: string
  alias: string
  email: string
  neptun: string
  guild: GuildType
  major: MajorType

  groupName: string
  groupLeaders: GroupLeaderView[]
  groupSelectionAllowed: boolean
  availableGroups: Record<number, string>
  fallbackGroup: number

  role: keyof typeof RoleType

  tokens: TokenView[]
  minTokenToComplete: number
  totalTokenCount: number
  collectedTokenCount: number

  totalRiddleCount: number
  completedRiddleCount: number

  totalTaskCount: number
  completedTaskCount: number
  submittedTaskCount: number

  locations: GroupMemberLocationView[]
  debts: DebtView[]
  leaderboard: TopListAbstractEntryView[]

  profileIsComplete: boolean
  incompleteTasks?: string[]
}

//cannot compare roles if the enums values are strings use the RoleType[role] syntax
export enum RoleType {
  GUEST = 0, // anyone without login
  BASIC = 1, // has auth.sch but not member of SSSL
  STAFF = 100, // member of the SSSL
  ADMIN = 200, // the organizers of the event
  SUPERUSER = 500 // advanced user management (able to grant admin access)
}

export enum GuildType {
  UNKNOWN = 'n/a',
  BLACK = 'fekete',
  BLUE = 'kék',
  RED = 'piros',
  WHITE = 'fehér',
  YELLOW = 'sárga'
}

export enum MajorType {
  UNKNOWN,
  IT,
  EE,
  BPROF
}
