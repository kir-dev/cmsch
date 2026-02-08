import type { TopListAbstractEntryView } from './toplistAbstractEntry.view'

export const TaskType = {
  TEXT: 'TEXT',
  IMAGE: 'IMAGE',
  BOTH: 'BOTH',
  ONLY_PDF: 'ONLY_PDF',
  ONLY_ZIP: 'ONLY_ZIP'
} as const
export type TaskType = (typeof TaskType)[keyof typeof TaskType]

export const TaskFormat = {
  TEXT: 'TEXT',
  NONE: 'NONE',
  CODE: 'CODE',
  FORM: 'FORM'
} as const
export type TaskFormat = (typeof TaskFormat)[keyof typeof TaskFormat]

export const TaskStatus = {
  ACCEPTED: 'ACCEPTED',
  NOT_LOGGED_IN: 'NOT_LOGGED_IN',
  NOT_SUBMITTED: 'NOT_SUBMITTED',
  REJECTED: 'REJECTED',
  SUBMITTED: 'SUBMITTED'
} as const
export type TaskStatus = (typeof TaskStatus)[keyof typeof TaskStatus]

export const TaskSubmissionStatus = {
  OK: 'OK',
  EMPTY_ANSWER: 'EMPTY_ANSWER',
  INVALID_IMAGE: 'INVALID_IMAGE',
  INVALID_PDF: 'INVALID_PDF',
  INVALID_ZIP: 'INVALID_ZIP',
  ALREADY_SUBMITTED: 'ALREADY_SUBMITTED',
  ALREADY_APPROVED: 'ALREADY_APPROVED',
  NO_ASSOCIATE_GROUP: 'NO_ASSOCIATE_GROUP',
  INVALID_TASK_ID: 'INVALID_TASK_ID',
  TOO_EARLY_OR_LATE: 'TOO_EARLY_OR_LATE',
  NO_PERMISSION: 'NO_PERMISSION',
  INVALID_BACKEND_CONFIG: 'INVALID_BACKEND_CONFIG'
} as const
export type TaskSubmissionStatus = (typeof TaskSubmissionStatus)[keyof typeof TaskSubmissionStatus]

export const TaskCategoryType = {
  REGULAR: 'REGULAR',
  PROFILE_REQUIRED: 'PROFILE_REQUIRED'
} as const
export type TaskCategoryType = (typeof TaskCategoryType)[keyof typeof TaskCategoryType]

export const CodeLanguage = {
  C: 'c',
  CPP: 'cpp',
  CSHARP: 'csharp',
  JAVA: 'java',
  JAVASCRIPT: 'javascript',
  TYPESCRIPT: 'typescript',
  SQL: 'sql',
  KOTLIN: 'kotlin',
  PYTHON: 'python'
} as const
export type CodeLanguage = (typeof CodeLanguage)[keyof typeof CodeLanguage]

export interface TaskFormatDescriptor {
  title: string
  type: 'number' | 'text' | 'textarea'
  suffix?: string
}

export interface TaskCategoryPreview {
  categoryId: number
  name: string
  approved: number
  availableFrom?: number
  availableTo?: number
  notGraded: number
  rejected: number
  sum: number
  type: TaskCategoryType
}

export interface TaskCategoryFullDetails {
  categoryName: string
  description: string
  tasks: TaskWrapper[]
}

export interface AllTaskCategories {
  score?: number
  categories: TaskCategoryPreview[]
  leaderboard: TopListAbstractEntryView[]
  leaderBoardVisible: boolean
  leaderBoardFrozen: boolean
}

export interface TaskEntity {
  id: number
  title: string
  categoryId: number
  categoryName?: string
  description: string
  expectedResultDescription: string
  type: TaskType
  format: TaskFormat
  formatDescriptor: string
  availableFrom: number
  availableTo: number
}

export interface TaskWrapper {
  task: TaskEntity
  response: string
  status: TaskStatus
}

export interface TaskFullDetailsView {
  task?: TaskEntity
  status: TaskStatus
  submission?: {
    id: number
    task?: TaskEntity
    groupId?: number
    groupName: string
    userId?: number
    userName: string
    categoryId: number
    textAnswer: string
    imageUrlAnswer: string
    fileUrlAnswer: string
    response: string
    approved: boolean
    rejected: boolean
    score?: number
  }
}
