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

export interface TaskCategoryPreview {
  categoryId: number
  name: string
  approved: number
  availableFrom?: number
  availableTo?: number
  notGraded: number
  rejected: number
  sum: number
}

export interface TaskCategoryFullDetails {
  categoryName: string
  tasks: TaskWrapper[]
}

export interface AllTaskCategories {
  score?: number
  categories: TaskCategoryPreview[]
  leaderboard: LeaderboardEntity[]
  leaderBoardVisible: boolean
  leaderBoardFrozen: boolean
}

export interface LeaderboardEntity {
  name: string
  totalScore: number
  riddleScore: number
  taskScore: number
}

export interface TaskEntity {
  id: number
  title: string
  categoryId: number
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
    score: number
  }
}
