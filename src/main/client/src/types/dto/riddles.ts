export interface Riddle {
  id: number
  title: string
  imageUrl: string
  solved: boolean
  hint?: string
}

export interface RiddleCategory {
  categoryId: number
  title: string
  score: number
  completed: number
  total: number
  nextRiddle?: number
}

export enum RiddleSubmissionStatus {
  CORRECT = 'CORRECT',
  WRONG = 'WRONG'
}

export interface RiddleSubmissionResult {
  status: RiddleSubmissionStatus
  nextId?: number
}

export interface Hint {
  hint: string
}
