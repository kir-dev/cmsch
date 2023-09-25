export interface Riddle {
  id: number
  title: string
  imageUrl: string
  solved: boolean
  description?: string
  hint?: string
  firstSolver?: string
  creator?: string
  skipPermitted: boolean
}

export interface RiddleWithSolution extends Riddle {
  solution: string
}

export interface RiddleCategoryHistory {
  categoryName: string
  submissions: RiddleWithSolution[]
}

export interface RiddleCategory {
  categoryId: number
  title: string
  completed: number
  total: number
  nextRiddle?: number
}

export enum RiddleSubmissionStatus {
  CORRECT = 'CORRECT',
  WRONG = 'WRONG',
  CANNOT_SKIP = 'CANNOT_SKIP',
  SUBMITTER_BANNED = 'SUBMITTER_BANNED'
}

export interface RiddleSubmissionResult {
  status: RiddleSubmissionStatus
  nextId?: number
}

export interface Hint {
  hint: string
}
