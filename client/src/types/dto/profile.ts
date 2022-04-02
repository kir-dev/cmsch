import { TokenDTO } from './token'

export interface ProfileDTO {
  fullName: string
  groupName: string
  role: keyof typeof RoleType
  tokens: TokenDTO[]
  totalTokenCount: number
  collectedTokenCount: number
  totalRiddleCount: number
  completedRiddleCount: number
  totalAchievementCount: number
  completedAchievementCount: number
  submittedAchievementCount: number
  minTokenToComplete: number
  groupSelectionAllowed: boolean
  fallbackGroup: number
  availableGroups: Record<number, string>
}

//cannot compare roles if the enums values are strings use the RoleType[role] syntax
export enum RoleType {
  GUEST = 0, // anyone without login
  BASIC = 1, // has auth.sch but not member of SSSL
  STAFF = 100, // member of the SSSL
  ADMIN = 200, // the organizers of the event
  SUPERUSER = 500 // advanced user management (able to grant admin access)
}
