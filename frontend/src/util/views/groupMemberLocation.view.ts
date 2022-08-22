export interface GroupMemberLocationView {
  id: number
  userId?: number
  userName?: string
  alias: string
  groupName?: string
  longitude: number
  latitude: number
  accuracy: number
  altitude?: number
  timestamp: number
}
