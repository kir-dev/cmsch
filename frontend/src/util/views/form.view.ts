import { Signup } from '../../api/contexts/config/types'

export type FormData = {
  form?: Form
  submission?: Record<string, string>
  status: FormStatus
  message?: string
  detailsValidated?: boolean
}

export type Form = {
  name: string
  url: string
  formFields: FormField[]
  availableFrom: number
  availableUntil: number
}

export type FormField = {
  fieldName: string
  label: string
  type: FormFieldVariants
  formatRegex: string
  invalidFormatMessage: string
  values: string
  note: string
  required: boolean
  permanent: boolean
  defaultValue: string
}

export enum FormStatus {
  NO_SUBMISSION = 'NO_SUBMISSION',
  SUBMITTED = 'SUBMITTED',
  REJECTED = 'REJECTED',
  ACCEPTED = 'ACCEPTED',
  TOO_EARLY = 'TOO_EARLY',
  TOO_LATE = 'TOO_LATE',
  NOT_ENABLED = 'NOT_ENABLED',
  NOT_FOUND = 'NOT_FOUND',
  FULL = 'FULL',
  GROUP_NOT_PERMITTED = 'GROUP_NOT_PERMITTED'
}

export const FormStatusLangKeys: Record<FormStatus, keyof Signup> = {
  [FormStatus.NO_SUBMISSION]: 'langNoSubmission',
  [FormStatus.SUBMITTED]: 'langSubmitted',
  [FormStatus.REJECTED]: 'langRejected',
  [FormStatus.ACCEPTED]: 'langAccepted',
  [FormStatus.TOO_EARLY]: 'langTooEarly',
  [FormStatus.TOO_LATE]: 'langTooLate',
  [FormStatus.NOT_ENABLED]: 'langNotEnabled',
  [FormStatus.NOT_FOUND]: 'langNotFound',
  [FormStatus.FULL]: 'langFull',
  [FormStatus.GROUP_NOT_PERMITTED]: 'langGroupInsufficient'
}

export enum FormFieldVariants {
  TEXT = 'TEXT',
  VOTE = 'VOTE',
  LONG_TEXT = 'LONG_TEXT',
  NUMBER = 'NUMBER',
  EMAIL = 'EMAIL',
  PHONE = 'PHONE',
  CHECKBOX = 'CHECKBOX',
  SELECT = 'SELECT',
  MUST_AGREE = 'MUST_AGREE',
  INFO_BOX = 'INFO_BOX',
  WARNING_BOX = 'WARNING_BOX',
  TEXT_BOX = 'TEXT_BOX',
  SECTION_START = 'SECTION_START'
}

export enum FormSubmitResult {
  OK = 'OK',
  OK_RELOG_REQUIRED = 'OK_RELOG_REQUIRED',
  ALREADY_SUBMITTED = 'ALREADY_SUBMITTED',
  INVALID_VALUES = 'INVALID_VALUES',
  FORM_NOT_AVAILABLE = 'FORM_NOT_AVAILABLE',
  FORM_IS_FULL = 'FORM_IS_FULL',
  EDIT_SUBMISSION_NOT_FOUND = 'EDIT_SUBMISSION_NOT_FOUND',
  EDIT_CANNOT_BE_CHANGED = 'EDIT_CANNOT_BE_CHANGED'
}

export const FormSubmitMessage: Record<FormSubmitResult, string> = {
  OK: 'Sikeres beküldés!',
  OK_RELOG_REQUIRED: 'Sikeres beküldés!',
  ALREADY_SUBMITTED: 'Már be lett küldve',
  INVALID_VALUES: 'Érvénytelen kitöltés.',
  FORM_NOT_AVAILABLE: 'Űrlap nem elérhető.',
  FORM_IS_FULL: 'Jelentkezés megtelt.',
  EDIT_SUBMISSION_NOT_FOUND: 'A kitöltés nem található, így nem módosítható.',
  EDIT_CANNOT_BE_CHANGED: 'Nem módosítható.'
}

export type VotingFieldOption = {
  title: string
  value: string
  img: string
  text: string
}
