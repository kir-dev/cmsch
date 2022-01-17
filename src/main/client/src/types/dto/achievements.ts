export enum achievementType {
  TEXT,
  IMAGE,
  BOTH
}

export enum achievementStatus {
  ACCEPTED,
  NOT_LOGGED_IN,
  NOT_SUBMITTED,
  REJECTED,
  SUBMITTED
}

export interface AchievementCategory {
  categoryId: number
  name: string
  approved?: number
  availableFrom?: number
  availableTo?: number
  notGraded?: number
  rejected?: number
  sum?: number
  // this won't come from the api, but it can be manually put here,
  // so it's easier to map through the categories
  achievements?: AchievementWrapper[]
}

export interface AchievementEntity {
  id: number
  categoryId: number
  title: string
  description: string
  type: achievementType
  availableFrom?: number
  availableTo?: number
  expectedResultDescription?: string
}

export interface AchievementWrapper {
  achievement: AchievementEntity
  response?: string
  status: achievementStatus
}

export interface AchievementSubmission {
  id: number
  approved: boolean
  imageUrlAnswer?: string
  textAnswer?: string
  score: number
  groupName?: string
  rejected: boolean
  response?: string
}

export interface AchievementFullDetailsView {
  achievement: AchievementEntity
  status: achievementStatus
  submission?: AchievementSubmission
}
