export type GroupChangeDTO = {
  status: GroupChangeStatus
}

export const GroupChangeStatus = {
  OK: 'OK',
  INVALID_GROUP: 'INVALID_GROUP',
  LEAVE_DENIED: 'LEAVE_DENIED',
  PERMISSION_DENIED: 'PERMISSION_DENIED',
  UNAUTHORIZED: 'UNAUTHORIZED'
} as const
export type GroupChangeStatus = (typeof GroupChangeStatus)[keyof typeof GroupChangeStatus]
