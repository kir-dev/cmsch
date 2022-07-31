import { TopListAbstractEntryView } from './toplistAbstractEntry.view'

export enum taskType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  BOTH = 'BOTH',
  ONLY_PDF = 'ONLY_PDF'
}

export enum taskFormat {
  TEXT = 'TEXT',
  NONE = 'NONE',
  CODE = 'CODE',
  FORM = 'FORM'
}

export enum taskStatus {
  ACCEPTED = 'ACCEPTED',
  NOT_LOGGED_IN = 'NOT_LOGGED_IN',
  NOT_SUBMITTED = 'NOT_SUBMITTED',
  REJECTED = 'REJECTED',
  SUBMITTED = 'SUBMITTED'
}

export enum taskSubmissionStatus {
  OK = 'OK',
  EMPTY_ANSWER = 'EMPTY_ANSWER',
  INVALID_IMAGE = 'INVALID_IMAGE',
  INVALID_PDF = 'INVALID_PDF',
  ALREADY_SUBMITTED = 'ALREADY_SUBMITTED',
  ALREADY_APPROVED = 'ALREADY_APPROVED',
  NO_ASSOCIATE_GROUP = 'NO_ASSOCIATE_GROUP',
  INVALID_TASK_ID = 'INVALID_TASK_ID',
  TOO_EARLY_OR_LATE = 'TOO_EARLY_OR_LATE',
  NO_PERMISSION = 'NO_PERMISSION',
  INVALID_BACKEND_CONFIG = 'INVALID_BACKEND_CONFIG'
}

export enum taskCategoryType {
  REGULAR = 'REGULAR',
  PROFILE_REQUIRED = 'PROFILE_REQUIRED'
}

export enum codeLanguage {
  C = 'c',
  CPP = 'cpp',
  CSHARP = 'csharp',
  JAVA = 'java',
  JAVASCRIPT = 'javascript',
  TYPESCRIPT = 'typescript',
  SQL = 'sql',
  KOTLIN = 'kotlin',
  PYTHON = 'python'
}

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
  type: taskCategoryType
}

export interface TaskCategoryFullDetails {
  categoryName: string
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
  type: taskType
  format: taskFormat
  formatDescriptor: string
  availableFrom: number
  availableTo: number
}

export interface TaskWrapper {
  task: TaskEntity
  response: string
  status: taskStatus
}

export interface TaskFullDetailsView {
  task?: TaskEntity
  status: taskStatus
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
