export interface Riddle {
  id: number
  title: string
  imageUrl: string
  solved: boolean
  hint?: string
}

export interface RiddleCategory {
  // id: number
  // name: string
  // completed: boolean
  // scoore: number
  // nextRiddle?: number
  id: number
  name: string
  score: number
  completed: number
  total: number
  nextRiddle: number
}

export enum RiddleSubmissonStatus {
  CORRECT = 'CORRECT',
  WRONG = 'WRONG'
}
export interface RiddleSubmissonResult {
  status: RiddleSubmissonStatus
  nextId?: number
}
