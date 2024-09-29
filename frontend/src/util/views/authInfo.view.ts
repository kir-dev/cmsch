import { RoleTypeString } from './profile.view.ts'

export enum AuthState {
  EXPIRED = 'EXPIRED',
  LOGGED_IN = 'LOGGED_IN',
  LOGGED_OUT = 'LOGGED_OUT'
}

export type UserAuthInfoView = {
  authState: AuthState
  id?: number
  internalId?: string
  role?: RoleTypeString
  permissionsAsList?: string[]
  userName?: string
  groupId?: number
  groupName?: string
}
