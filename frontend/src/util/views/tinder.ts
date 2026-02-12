export interface TinderQuestion {
  id: number
  question: string
  options: string[]
}

export interface TinderAnswer {
  questionId: number
  answer: string
}

enum TinderStatus {
  NOT_SEEN = 'NOT_SEEN',
  LIKED = 'LIKED',
  DISLIKED = 'DISLIKED'
}

export interface TinderCommunity {
  id: number
  name: string
  matchedAnswers: number
  status: TinderStatus
  shortDescription: string
  descriptionParagraphs: string
  website: string
  logo: string
  established: string
  email: string
  interests: string[]
  facebook: string
  instagram: string
  application: string
  resortName: string
  tinderAnswers: string[]
}

export interface SendAnswerDto {
  answers: Map<number, string>
}

export enum SendAnswerResponseStatus {
  OK = 'OK',
  INVALID_ANSWER = 'INVALID_ANSWER',
  NO_PERMISSION = 'NO_PERMISSION',
  ERROR = 'ERROR'
}

export interface TinderInteractionDto {
  communityId: number
  liked: boolean
}
