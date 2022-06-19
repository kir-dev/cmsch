export type GroupChangeDTO = {
  status: GroupChangeStatus
}

export enum GroupChangeStatus {
  OK = 'OK',
  INVALID_GROUP = 'INVALID_GROUP',
  LEAVE_DENIED = 'LEAVE_DENIED',
  PERMISSION_DENIED = 'PERMISSION_DENIED',
  UNAUTHORIZED = 'UNAUTHORIZED'
}
