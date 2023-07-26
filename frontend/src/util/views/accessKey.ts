export interface AccessKey {
  title: string
  topMessage: string
  fieldName: string
  enabled: boolean
}

export interface AccessKeyRequest {
  key: string
}

export interface AccessKeyResponse {
  success: boolean
  reason?: string
  refreshSession: boolean
}
