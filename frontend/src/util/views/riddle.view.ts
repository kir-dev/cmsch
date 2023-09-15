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
  score: number
  completed: number
  total: number
  nextRiddle?: number
}

export enum RiddleSubmissonStatus {
  CORRECT = 'CORRECT',
  WRONG = 'WRONG'
}

export interface RiddleSubmissonResult {
  status: RiddleSubmissonStatus
  nextId?: number
}

export interface Hint {
  hint: string
}
