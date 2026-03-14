import type { LoginStatus } from '@/api/hooks/auth/useAuthActions'
import { l } from '@/util/language'

export const getMessageFromStatus = (status: LoginStatus) => {
  switch (status) {
    case 'OK':
      return l('login-status-ok')
    case 'ERROR':
      return l('login-status-error')
    case 'INVALID_CAPTCHA':
      return l('login-status-invalid-captcha')
    case 'MISSING_FIELDS':
      return l('login-status-missing-fields')
    case 'EMAIL_ALREADY_EXISTS':
      return l('login-status-email-already-exists')
    case 'EMAIL_NOT_CONFIRMED':
      return l('login-status-email-not-confirmed')
    case 'INVALID_CREDENTIALS':
      return l('login-status-invalid-credentials')
    case 'DISABLED':
      return l('login-status-disabled')
    case 'RATE_LIMITED':
      return l('login-status-rate-limited')
    case 'INVALID_TOKEN':
      return l('login-status-invalid-token')
    case 'TOKEN_EXPIRED':
      return l('login-status-token-expired')
    default:
      return l('login-status-error')
  }
}
