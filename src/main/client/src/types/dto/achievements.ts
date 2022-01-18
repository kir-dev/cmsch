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

export interface AchievementsInCategory {
  achievements: AchievementWrapper[]
  categoryName: string
}

export interface AllAchievementCategories {
  categories: AchievementCategory[]
  leaderboard?: [
    {
      name: string
      score: number
    }
  ]
  leaderBoardVisible?: boolean
  leaderBoardFrozen?: boolean
}

export interface AchievementEntity {
  id: number
  categoryId: number
  title: string
  description: string
  type: achievementType
  expectedResultDescription?: string
  availableFrom?: number
  availableTo?: number
}

export interface AchievementWrapper {
  achievement: AchievementEntity
  response?: string
  status: achievementStatus
}

export interface AchievementFullDetailsView {
  achievement?: AchievementEntity
  status: achievementStatus
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
