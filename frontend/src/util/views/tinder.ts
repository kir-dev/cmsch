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
  TINDER_NOT_AVAILABLE = 'TINDER_NOT_AVAILABLE',
  NO_PERMISSION = 'NO_PERMISSION',
  ERROR = 'ERROR'
}

export const SendAnswerResponseMessage: Record<SendAnswerResponseStatus, string> = {
  [SendAnswerResponseStatus.OK]: 'Válaszok sikeresen elmentve.',
  [SendAnswerResponseStatus.INVALID_ANSWER]: 'Érvénytelen válasz(ok). Kérjük, ellenőrizze a válaszait.',
  [SendAnswerResponseStatus.TINDER_NOT_AVAILABLE]: 'A Tinder jelenleg nem elérhető.',
  [SendAnswerResponseStatus.NO_PERMISSION]: 'Nincs jogosultsága válaszokat küldeni.',
  [SendAnswerResponseStatus.ERROR]: 'Hiba történt a válaszok mentése során. Kérjük, próbálja újra később.'
}

export interface TinderInteractionDto {
  communityId: number
  liked: boolean
}
