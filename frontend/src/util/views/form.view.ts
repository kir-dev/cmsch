export type FormDataDto = {
  form: FormDto
  status: FormStatus
}

export type FormData = {
  form: Form
  status: FormStatus
}

export type FormDto = {
  name: string
  url: string
  formJson: string
  availableFrom: number
  availableUntil: number
}

export type Form = {
  name: string
  url: string
  formJson: FormField[]
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
  FULL = 'FULL'
}

export enum FormFieldVariants {
  TEXT = 'TEXT',
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
