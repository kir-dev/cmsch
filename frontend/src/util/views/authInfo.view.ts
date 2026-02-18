import { RoleType } from './profile.view.ts'

export const AuthState = {
  EXPIRED: 'EXPIRED',
  LOGGED_IN: 'LOGGED_IN',
  LOGGED_OUT: 'LOGGED_OUT'
}
export type AuthState = (typeof AuthState)[keyof typeof AuthState]

export type UserAuthInfoView = {
  authState: AuthState
  id?: number
  internalId?: string
  role?: keyof typeof RoleType
  permissionsAsList?: string[]
  userName?: string
  groupId?: number
  groupName?: string
}
