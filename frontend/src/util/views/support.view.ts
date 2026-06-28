export const SupportThreadStatus = {
  WAITING_FOR_ADMIN: 'WAITING_FOR_ADMIN',
  WAITING_FOR_CUSTOMER: 'WAITING_FOR_CUSTOMER',
  DONE: 'DONE'
} as const
export type SupportThreadStatus = (typeof SupportThreadStatus)[keyof typeof SupportThreadStatus]

export interface SupportThreadView {
  id: number
  uuid: string
  title: string
  status: SupportThreadStatus
  solver: string
  createdAt: number
  updatedAt: number
  userEmail: string
  userName: string
}

export interface SupportThreadEntity extends SupportThreadView {
  uuidSecret: string
  userInternalId: string
}

export interface PublicMessageView {
  id: number
  threadUuid: string
  content: string
  createdAt: number
  authorName: string
  fromAdmin: boolean
}

export interface ThreadDetailResponse {
  thread: SupportThreadView
  messages: PublicMessageView[]
  canReply: boolean
}

export interface ThreadListResponse {
  threads: SupportThreadEntity[]
}
