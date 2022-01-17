import { TokenDTO } from './token'

export enum RoleType {
  GUEST = 'GUEST', // anyone without login
  BASIC = 'BASIC', // has auth.sch but not member of SSSL
  STAFF = 'STAFF', // member of the SSSL
  ADMIN = 'ADMIN', // the organizers of the event
  SUPERUSER = 'SUPERUSER' // advanced user management (able to grant admin access)
}

export interface ProfileDTO {
  completedRiddleCount: number
  fullName: string
  groupName: string
  role: RoleType
  submittedAchievementCount: number
  tokens: TokenDTO[]
  totalAchievementCount: number
  totalRiddleCount: number
  totalTokenCount: number
}
