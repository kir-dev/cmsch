export enum taskType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  BOTH = 'BOTH'
}

export enum taskStatus {
  ACCEPTED = 'ACCEPTED',
  NOT_LOGGED_IN = 'NOT_LOGGED_IN',
  NOT_SUBMITTED = 'NOT_SUBMITTED',
  REJECTED = 'REJECTED',
  SUBMITTED = 'SUBMITTED'
}

export interface TaskCategory {
  categoryId?: number
  name: string
  approved: number
  availableFrom?: number
  availableTo?: number
  notGraded: number
  rejected: number
  sum: number
  tasks?: TaskWrapper[]
  categoryName?: string
}

export interface AllTaskCategories {
  categories: TaskCategory[]
  leaderboard?: [
    {
      name: string
      score: number
    }
  ]
  leaderBoardVisible?: boolean
  leaderBoardFrozen?: boolean
}

export interface TaskEntity {
  id: number
  categoryId: number
  title: string
  description: string
  type: taskType
  expectedResultDescription?: string
  availableFrom: number
  availableTo: number
}

export interface TaskWrapper {
  task: TaskEntity
  response?: string
  status: taskStatus
}

export interface TaskFullDetailsView {
  task?: TaskEntity
  status: taskStatus
  submission?: {
    id: number
    approved: boolean
    imageUrlAnswer?: string
    textAnswer?: string
    score: number
    groupName?: string
    rejected: boolean
    response?: string
  }
}
